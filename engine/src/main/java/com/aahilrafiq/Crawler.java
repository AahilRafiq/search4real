package com.aahilrafiq;

import com.aahilrafiq.helpers.crawling.CrawlContainer;
import com.aahilrafiq.helpers.crawling.Worker;

import java.util.List;
import java.util.Arrays;

public class Crawler {
    public static void main(String[] args) throws Exception{
        CrawlContainer container = CrawlContainer.getInstance();
        SiteData.topWebsites.forEach(container::enQueue);

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



class SiteData {
    public static final List<String> topWebsites = Arrays.asList(
            "https://google.com",
            "https://youtube.com",
            "https://facebook.com",
            "https://instagram.com",
            "https://wikipedia.org",
            "https://reddit.com",
            "https://bing.com",
            "https://chatgpt.com",
            "https://x.com",
            "https://yandex.ru",
            "https://whatsapp.com",
            "https://amazon.com",
            "https://yahoo.com",
            "https://duckduckgo.com",
            "https://netflix.com",
            "https://tiktok.com",
            "https://twitter.com",
            "https://msn.com",
            "https://linkedin.com",
            "https://live.com",
            "https://office.com",
            "https://vk.com",
            "https://twitch.tv",
            "https://naver.com",
            "https://baidu.com",
            "https://roblox.com",
            "https://pinterest.com",
            "https://quora.com",
            "https://discord.com",
            "https://canva.com",
            "https://github.com",
            "https://apple.com",
            "https://spotify.com",
            "https://imdb.com",
            "https://cnn.com",
            "https://nytimes.com",
            "https://ebay.com",
            "https://bbc.com",
            "https://paypal.com",
            "https://adobe.com",
            "https://walmart.com",
            "https://zillow.com",
            "https://etsy.com",
            "https://weather.com",
            "https://booking.com",
            "https://indeed.com",
            "https://zoom.us",
            "https://flipkart.com"
    );
}

