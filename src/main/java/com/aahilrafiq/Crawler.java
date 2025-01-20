package com.aahilrafiq;

import com.aahilrafiq.helpers.crawling.CrawlContainer;
import com.aahilrafiq.helpers.crawling.Worker;

public class Crawler {
    public static void main(String[] args) throws Exception{
        CrawlContainer container = CrawlContainer.getInstance();
        container.enQueue("https://github.com/");

        final int numThreads = 10;
        Worker[] workers = new Worker[numThreads];
        for(int i=0 ; i<numThreads ; i++) {
            workers[i] = new Worker();
            System.out.flush();
        }

        // Start workers
        for(Worker worker : workers) {
            worker.start();
        }
        for(Worker worker : workers) {
            worker.join();
        }
    }
}

