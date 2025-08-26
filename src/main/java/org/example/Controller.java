package org.example;

import com.mongodb.client.*;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.bson.Document;

public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 2;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(1000);
        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://en.wikipedia.org/wiki/Kazakhstan");

        // The factory which creates instances of crawlers.
        String connectionString = "mongodb://localhost:27017/?maxPoolSize=50";
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("testdb");
            System.out.println("Connected to database: " + database.getName());
            MongoCollection<Document> collection = database.getCollection("pages");

            collection.createIndex(
                    new Document("TEXT", "text").append("title", "text")
            );
            if (false){
                controller.start(new MongoDBCrawlerFactory(mongoClient, database), numberOfCrawlers);

            } else {
                MongoShower s = new MongoShower(collection);
                String searchWord = "Kazakhstan";
                s.find(searchWord);
            }





        }


    }
        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.

}
