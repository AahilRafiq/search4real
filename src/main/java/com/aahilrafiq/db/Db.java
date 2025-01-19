package com.aahilrafiq.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private static Connection conn;

    public static Connection getConnection() {
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
}