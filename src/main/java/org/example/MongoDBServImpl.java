package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.slf4j.Logger;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;

import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Indexes.text;
import static com.mongodb.client.model.Sorts.metaTextScore;

public class MongoDBServImpl implements MongoDBService {
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private HashSet<String> set = new HashSet<>();
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MongoDBServImpl.class);

    public MongoDBServImpl(MongoClient mongoClient, MongoDatabase mongoDatabase) throws RuntimeException {
        this.mongoClient = mongoClient;
        this.mongoDatabase = mongoDatabase;


    }

    @Override
    public void store(Page webPage) {
        if (webPage.getParseData() instanceof HtmlParseData) {
            try {
                HtmlParseData data = (HtmlParseData) webPage.getParseData();
                MongoCollection<Document> collection = this.mongoDatabase.getCollection("pages");

                Document doc =
                        new Document("HTML", data.getHtml())
                                .append("TEXT", data.getText())
                                .append("URL", webPage.getWebURL().getURL())
                                .append("Title", data.getTitle());
                if (!set.contains(data.getTitle()))
                    collection.insertOne(doc);
            } catch (RuntimeException e) {
                logger.error("Some Exception while storing webpage for url'{}'", webPage.getWebURL().getURL(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void drop() {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection("pages");
        collection.drop();
    }

    @Override
    public void show() {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection("pages");

        String searchWord = "Kazakhstan";
        Document search = new Document("$text", new Document("$search", searchWord));
        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> results = collection.find(search)
                .projection(projection)
                .sort(projection);

        for (Document d : results) {
            System.out.println("Something " + d.getString("Title") + " | score=" + d.get("score"));
        }
    }


    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

}
