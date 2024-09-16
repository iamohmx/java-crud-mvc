package com.storecrud.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.mariadb.jdbc.Statement;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storecrud.core.MariaDB;
import com.storecrud.models.Category;

public class CategoryController {
    private Vector<Category> Categories;

    // get timestamp
    public static java.sql.Timestamp getCurrentTimeStamp(){
        java.util.Date toDay = new java.util.Date();
        return new java.sql.Timestamp(toDay.getTime());
    }

    public CategoryController () {
        Categories = new <Category> Vector();
    }

    public String viewCategories(){
        String SQL_SELECT = "SELECT cat_id, cat_name FROM categories;";
        Vector<Category> categories = new Vector();

        MariaDB conDb = new MariaDB();
        Connection conn = conDb.getConnection();

        if (conn != null) {
            try {
                
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("cat_id");
                    String cat_name = rs.getString("cat_name");

                    Category cat = new Category(id, cat_name);
                    categories.add(cat);
                }

                preparedStatement.close();
                conn.close();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResponse = gson.toJson(categories);
        return jsonResponse;
    }

    public void addCategory() {

        MariaDB connDb = new MariaDB();
        Connection conn = connDb.getConnection();
    
        try (Scanner sc = new Scanner(System.in)) {
    
            System.out.print("Category name: ");
            String cat_name = sc.nextLine();
            Category cat = new Category(cat_name);
            String SQL_INSERT = "INSERT INTO `categories`(`cat_name`) VALUES (?)";
    
            if (conn != null) {
                try (PreparedStatement addPreparedStatement = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    
                    addPreparedStatement.setString(1, cat.getName());
                    int row = addPreparedStatement.executeUpdate();
                    
                    if (row > 0) {
                        System.out.println("Category added successfully!");
    
                        // ดึงค่า cat_id ของหมวดหมู่ที่ถูกแทรกล่าสุด
                        ResultSet generatedKeys = addPreparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int lastInsertedId = generatedKeys.getInt(1); // ค่าของ cat_id ที่เพิ่งแทรก
    
                            // ดึงข้อมูลหมวดหมู่ที่เพิ่มล่าสุด
                            String SQL_SELECT = "SELECT cat_id, cat_name FROM categories WHERE cat_id = ?;";
                            
                            try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
                                preparedStatement.setInt(1, lastInsertedId);
                                ResultSet rs = preparedStatement.executeQuery();
                                
                                if (rs.next()) {
                                    int id = rs.getInt("cat_id");
                                    String name = rs.getString("cat_name");
    
                                    Category lastInsertedCategory = new Category(id, name);
    
                                    // สร้าง JSON response 
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    String jsonResponse = gson.toJson(lastInsertedCategory);
                                    System.out.println(jsonResponse);
                                }
                            }
                        }
                    } else {
                        System.out.println("Failed to add category.");
                    }
                } catch (SQLException e) {
                    System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // ปิด Connection
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Failed to establish connection.");
            }
        }
    }    
    
     
}
