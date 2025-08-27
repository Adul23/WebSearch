package com.example.demo1;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DB_NAME = "testdb";
    private static MongoClient mongoClient;

    static {
        mongoClient = MongoClients.create(CONNECTION_STRING);
    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase(DB_NAME);
    }
}
