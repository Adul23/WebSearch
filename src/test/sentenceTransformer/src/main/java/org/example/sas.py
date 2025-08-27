from pymongo import MongoClient
from sentence_transformers import SentenceTransformer

client = MongoClient("mongodb://localhost:27017")
db = client["demo1"]
collection = db["pages"]

model = SentenceTransformer("all-MiniLM-L6-v2")

for doc in collection.find():
    text = doc["Title"] + " " + doc.get("Description", "")
    embedding = model.encode(text).tolist()
    collection.update_one({"_id": doc["_id"]}, {"$set": {"embedding": embedding}})
