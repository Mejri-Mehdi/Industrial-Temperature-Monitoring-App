package com.example.vernicolorapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDatabase {
    // SQL Server connection URL with integrated security
    private static final String URL = "jdbc:sqlserver://DESKTOP-C510AIL\\SQLEXPRESS;databaseName=TemperatureMonitor;integratedSecurity=true";

    // No need for USER and PASS when using integrated security
    private static final String USER = "";
    private static final String PASS = "";

    // Method to establish a connection to the SQL Server
    public Connection connect() {
        Connection conn = null;
        try {
            // Load the SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connection to SQL Server successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection failed!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("SQL Server JDBC Driver not found!");
        }
        return conn;
    }
}
