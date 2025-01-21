package com.aahilrafiq.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

public class Db {

    private static Connection conn;
    private static Driver neo4jDriver;

    public static Connection getPostgresConnection() {
        if(conn != null) return conn;
        try {
            String jdbcUrl = "jdbc:postgresql://localhost:5432/search4real";
            String username = "postgres";
            String password = "password";
            conn = DriverManager.getConnection(jdbcUrl,username,password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return conn;
    }

    public static Driver getNeo4jDriver() {
        if(neo4jDriver != null) return neo4jDriver;
        String uri = "bolt://localhost:7687";
        String username = "neo4j";
        String password = "password";

        try {
            neo4jDriver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return neo4jDriver;
    }
}