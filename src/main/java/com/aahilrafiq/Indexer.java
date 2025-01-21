package com.aahilrafiq;
import com.aahilrafiq.db.Db;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import java.sql.*;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Indexer {
    public static void main(String[] args) throws SQLException {
        Driver neo4jDriver = Db.getNeo4jDriver();
        Connection db = Db.getPostgresConnection();
        int skipCnt = 0, processedSites = 0;
        int limitCnt = 100, totalSites = 0;

        // Get total count
        try (Statement getCount = db.createStatement();
             ResultSet res = getCount.executeQuery("select COUNT(*) from public.\"Sites\"")) {
            if (res.next()) {
                totalSites = Integer.parseInt(res.getString(1));
                System.out.println("Total sites to process: " + totalSites);
            }
        }

        try (Session session = neo4jDriver.session()) {
            while (processedSites < totalSites) {
                // Get batch of sites
                try (PreparedStatement sql = db.prepareStatement(
                        "select * from public.\"Sites\" offset ? limit ?")) {
                    sql.setInt(1, skipCnt);
                    sql.setInt(2, limitCnt);

                    try (ResultSet sites = sql.executeQuery()) {
                        // Process batch in single transaction
                        session.executeWrite(tx  -> {
                            try {
                                while (sites.next()) {
                                    int id = sites.getInt(1);
                                    String title = sites.getString(3);
                                    String desc = sites.getString(4);
                                    String words = sites.getString(5);

                                    // Create site node
                                    tx.run("MERGE (:Site {id: $id})",
                                            parameters("id", id));

                                    // Process words and weights
                                    Map<String, Integer> wordWeights = new HashMap<>();

                                    // Title words (weight 100)
                                    Arrays.stream(title.split("\\s+"))
                                            .forEach(word -> wordWeights.merge(word, 100, Integer::sum));

                                    // Description words (weight 10)
                                    Arrays.stream(desc.split("\\s+"))
                                            .forEach(word -> wordWeights.merge(word, 10, Integer::sum));

                                    // LATER : Content words (weight 1)
                                    //Arrays.stream(words.split("\\s+"))
                                    //.forEach(word -> wordWeights.merge(word, 1, Integer::sum));

                                    // Batch create words and relationships
                                    for (Map.Entry<String, Integer> entry : wordWeights.entrySet()) {
                                        String word = entry.getKey();
                                        int weight = entry.getValue();

                                        tx.run("""
                                                        MERGE (w:Word {text: $text})
                                                        WITH w
                                                        MATCH (s:Site {id: $id})
                                                        MERGE (w)-[r:FOUND_IN]->(s)
                                                        SET r.weight = $weight
                                                        """,
                                                parameters("text", word, "id", id, "weight", weight));
                                    }
                                }
                            } catch (SQLException e) {
                                System.err.println(e.getMessage());
                            }
                            return null;
                        });
                    }
                }

                skipCnt += limitCnt;
                processedSites += limitCnt;
                System.out.println("Processed " + processedSites + " sites");
            }
        }
    }
}