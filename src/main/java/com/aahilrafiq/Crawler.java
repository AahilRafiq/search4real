package com.aahilrafiq;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Crawler {
    public static void main(String[] args) throws Exception{
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_most-visited_websites").get();
        System.out.println(doc.title());
        System.out.println(doc.select("a"));
    }
}
