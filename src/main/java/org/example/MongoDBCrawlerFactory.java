package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;

public class MongoDBCrawlerFactory implements CrawlController.WebCrawlerFactory<Crawler>{
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    public MongoDBCrawlerFactory(MongoClient mongoClient, MongoDatabase mongoDatabase){
        this.mongoClient = mongoClient;
        this.database = mongoDatabase;
    }

    @Override
    public Crawler newInstance() throws Exception {
        return new Crawler(new MongoDBServImpl(this.mongoClient, this.database));
    }
}
