package com.aahilrafiq;

import com.aahilrafiq.helpers.CrawlContainer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.aahilrafiq.db.Db;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Crawler {
    public static void main(String[] args) throws Exception{
        CrawlContainer container = CrawlContainer.getInstance();
        container.enQueue("https://github.com/");

        final int numThreads = 6;
        Worker[] workers = new Worker[numThreads];
        for(int i=0 ; i<numThreads ; i++) {
            workers[i] = new Worker();
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

class Worker extends Thread {
    public void run() {
        Connection db = Db.getConnection();
        CrawlContainer container = CrawlContainer.getInstance();
        int maxTries = 100;

        while(maxTries > 0) {
            String currSite = container.deQueue();
            if (currSite == null) {
                try {
                    Thread.sleep(10000);
                    maxTries--;
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Document doc;

            try{
                doc = Jsoup.connect(currSite).get();

                String title = doc.title().replaceAll("[^A-Za-z0-9]"," ");

                String desc = null;
                Element metaDescription = doc.selectFirst("meta[name=description]");
                if(metaDescription != null) {
                    desc = metaDescription.attr("content").replaceAll("[^A-Za-z0-9]"," ");;
                }

                String bodyText = doc.body().text().replaceAll("[^A-Za-z0-9]"," ");

                PreparedStatement sql = db.prepareStatement("INSERT INTO public.\"Sites\" (link,title,\"desc\",words) VALUES (?,?,?,?)");
                sql.setString(1,currSite);
                sql.setString(2,title);
                sql.setString(3,desc == null ? "" : desc);
                sql.setString(4,bodyText);
                sql.execute();


                Elements anchorTags = doc.select("a");
                for(Element a : anchorTags) {
                    String absURL = a.attr("abs:href");
                    if(container.insertIfNotPresent(absURL)) {
                        System.out.println(absURL);
                        container.enQueue(absURL);
                    }
                }
                Thread.sleep(100);
            } catch (IOException | InterruptedException | SQLException e) {
                e.printStackTrace();
            }

            maxTries--;
        }
    }
}
