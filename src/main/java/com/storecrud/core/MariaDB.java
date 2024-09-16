package com.storecrud.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDB {
    DBConnection mariadb = new DBConnection(
        "jdbc:mariadb://localhost:3306/storecrud",
        "root",
        "root");
    
    
    public Connection getConnection() {
        
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            System.out.println("Driver Not Found!");
            e.printStackTrace();
        }

        Connection connection = null;
        // System.out.println("Database has connected successfully!");
        

        try {
            connection = DriverManager.getConnection(
                mariadb.server,
                mariadb.username, 
                mariadb.password
                );
        } catch (SQLException e) {
            // TODO: handle exce  ption
            System.out.println("Connection Faild!");
            e.getStackTrace();
        }

        if (connection == null) {
            System.out.println("Faild to connecting database...");
        }

        // System.out.printf("Connected to %s\n", mariadb.server);

        return connection;

    }
}
