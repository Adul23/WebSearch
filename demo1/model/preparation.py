from pymongo import MongoClient
from sentence_transformers import SentenceTransformer

model = SentenceTransformer("all-MiniLM-L6-v2")
client = MongoClient("mongodb://localhost:27017")
db = client["testdb"]
collection = db["pages"]
# print(collection.count_documents({}))
for doc in collection.find():
    text = doc["Title"] + " " + doc.get("TEXT")
    embedding = model.encode(text).tolist()
    collection.update_one({"_id": doc["_id"]}, {"$set": {"embedding": embedding}})
