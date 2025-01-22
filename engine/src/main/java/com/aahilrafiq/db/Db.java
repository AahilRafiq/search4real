package com.aahilrafiq.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import io.github.cdimascio.dotenv.Dotenv;

public class Db {

    private static Connection conn;
    private static Driver neo4jDriver;

    public static Connection getPostgresConnection() {
        if(conn != null) return conn;
        try {
            Dotenv dotenv = Dotenv.load();
            String jdbcUrl = dotenv.get("POSTGRES_URI");
            String username = dotenv.get("POSTGRES_USER");
            String password = dotenv.get("POSTGRES_PASS");
            conn = DriverManager.getConnection(jdbcUrl,username,password);
        } catch (SQLException e) {
            throw new Error(e);
        }

        return conn;
    }

    public static Driver getNeo4jDriver() {
        if(neo4jDriver != null) return neo4jDriver;
        Dotenv dotenv = Dotenv.load();
        String uri = dotenv.get("NEO4j_URI");
        String username = dotenv.get("NEO4j_USER");
        String password = dotenv.get("NEO4j_PASS");

        try {
            neo4jDriver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return neo4jDriver;
    }
}