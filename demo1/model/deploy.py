import redis, json, numpy as np
from pymongo import MongoClient
from sentence_transformers import SentenceTransformer

# Redis + Mongo connection
r = redis.Redis(host="localhost", port=6379, db=0)
client = MongoClient("mongodb://localhost:27017")
db = client["testdb"]
collection = db["pages"]

# BERT model
model = SentenceTransformer("all-MiniLM-L6-v2")

def cosine_sim(a, b):
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))

pubsub = r.pubsub()
pubsub.subscribe("search_requests")

print("Worker listening on 'search_requests'...")

for message in pubsub.listen():
    if message["type"] == "message":
        data = json.loads(message["data"].decode("utf-8"))
        query = data["query"]
        print(f"Query: {query}")

        # Encode query
        q_vec = model.encode(query)

        # Compare with DB embeddings
        results = []
        for doc in collection.find({}, {"Title": 1, "URL": 1, "embedding": 1}):
            if "embedding" not in doc:
                continue
            d_vec = np.array(doc["embedding"])
            sim = cosine_sim(q_vec, d_vec)
            results.append((sim, doc["Title"], doc["URL"]))

        # Top 5 results
        results = sorted(results, key=lambda x: x[0], reverse=True)[:5]

        # Build JSON
        response = [{"title": t, "url": u, "score": float(s)} for s, t, u in results]

        # Publish to Redis
        r.publish("search_results", json.dumps({"query": query, "results": response}))
        print("Published search results.")
