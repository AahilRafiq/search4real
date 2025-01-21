package com.aahilrafiq.helpers.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Worker extends Thread {
    public void run() {
        CrawlContainer container = CrawlContainer.getInstance();
        int maxTries = 10000;

        while(maxTries > 0) {
            String currSite = container.deQueue();
            if (currSite == null) {
                try {
                    Thread.sleep(10000);
                    maxTries--;
                    continue;
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }

            Document doc;

            try{
                doc = Jsoup.connect(currSite).get();

                String title = doc.title().replaceAll("[^A-Za-z0-9]"," ");
                String desc = null;
                Element metaDescription = doc.selectFirst("meta[name=description]");
                if(metaDescription != null) {
                    desc = metaDescription.attr("content").replaceAll("[^A-Za-z0-9]"," ");
                }
                String words = doc.body().text();

                FilterNStore.insertDb(currSite,title,desc,words);

                Elements anchorTags = doc.select("a");
                for(Element a : anchorTags) {
                    String absURL = a.attr("abs:href");
                    if(container.insertIfNotPresent(absURL)) {
                        System.out.println(absURL);
                        container.enQueue(absURL);
                    }
                }
                Thread.sleep(40);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }

            maxTries--;
        }
    }
}
