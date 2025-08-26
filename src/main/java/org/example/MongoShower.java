package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoShower {
    MongoCollection<Document> collection;
    public MongoShower(MongoCollection<Document> collection){
        this.collection = collection;
    }
    public void find(String searchWord){
        Document search = new Document("$text", new Document("$search", searchWord));
        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> results = collection.find(search)
                .projection(projection)
                .sort(projection);

        for (Document d : results){
            System.out.println(d.getString("Title") + " | score=" + d.get("score"));
        }
    }

}
