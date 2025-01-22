package com.aahilrafiq.helpers.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import static org.neo4j.driver.Values.parameters;

import com.aahilrafiq.db.Db;

public class QueryHelpers {
    public static List<ResponseObj> getResults(String query) {
        Driver driver = Db.getNeo4jDriver();
        Connection conn = Db.getPostgresConnection();
        List<Integer> siteIDs = new ArrayList<>();

        query = query.toLowerCase();
        query = query.replaceAll("[^A-Za-z0-9]"," ");
        String[] queryWords = query.split("\\s+");

        try(Session s = driver.session()) {
            String queryString = """
                    UNWIND $words as word
                    MATCH (w:Word {text: word})-[r:FOUND_IN]->(s:Site)
                    WITH s, sum(r.weight) as score
                    ORDER BY score DESC
                    LIMIT 15
                    RETURN s.id, score
                    """;

             s.executeWrite(tx -> {
                Result res = tx.run(queryString, parameters("words",queryWords));
                while(res.hasNext()) {
                    Record record = res.next();
                    siteIDs.add(record.get("s.id").asInt());
                }
                return null;
            });
        }

        List<ResponseObj> searchResults = new ArrayList<>();
        if(siteIDs.isEmpty()) return searchResults;

        try{

            String placeholders = String.join(",", Collections.nCopies(siteIDs.size(), "?"));
            String queryString = String.format("""
                select link,rawtitle,description from public."Sites"
                where id in (%s)
                """,placeholders);

            PreparedStatement sql = conn.prepareStatement(queryString);

            for (int i = 0; i < siteIDs.size(); i++) {
                sql.setInt(i + 1, siteIDs.get(i));
            }

            ResultSet res = sql.executeQuery();
            while(res.next()) {
                searchResults.add(new ResponseObj(res.getString(1),res.getString(2)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return searchResults;
    }
}
