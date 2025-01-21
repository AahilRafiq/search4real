package com.aahilrafiq.helpers.crawling;

import com.aahilrafiq.db.Db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FilterNStore {
    public static void insertDb(String link, String title, String desc, String words) {
        CrawlContainer container = CrawlContainer.getInstance();
        Connection db = Db.getPostgresConnection();

        // 1. Remove special characters
        title = title.replaceAll("[^A-Za-z0-9]"," ");
        if(desc!=null) desc = desc.replaceAll("[^A-Za-z0-9]"," ");
        words = words.replaceAll("[^A-Za-z0-9]"," ");

        // 2. Load stop words in a Set
        HashSet<String> stopWordsSet = new HashSet<>();
        InputStream inputStream = FilterNStore.class.getClassLoader().getResourceAsStream("stopwords.txt");
        if(inputStream != null) {
            Scanner scanner = new Scanner(inputStream);

            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();
                if (!word.isEmpty()) {
                    stopWordsSet.add(word.toLowerCase());
                }
            }

        }

        // Filter stop words
        title = Arrays.stream(title.split("\\s+")).filter(word -> !stopWordsSet.contains(word.toLowerCase())).collect(Collectors.joining(" "));
        if(desc!=null) desc = Arrays.stream(desc.split("\\s+")).filter(word -> !stopWordsSet.contains(word.toLowerCase())).collect(Collectors.joining(" "));
        words = Arrays.stream(words.split("\\s+")).filter(word -> !stopWordsSet.contains(word.toLowerCase())).collect(Collectors.joining(" "));

        try {
            PreparedStatement sql = db.prepareStatement("INSERT INTO public.\"Sites\" (link,title,description,words) VALUES (?,?,?,?)");
            sql.setString(1,link);
            sql.setString(2,title);
            sql.setString(3,desc == null ? "" : desc);
            sql.setString(4,words);
            sql.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}