package org.example;

import edu.uci.ics.crawler4j.crawler.Page;

public interface MongoDBService {
    void store(Page webPage);
    void drop();
    void close();
    void show();
}
