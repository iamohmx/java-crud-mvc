package com.storecrud.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import java.sql.Timestamp;

import org.mariadb.jdbc.Statement;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storecrud.core.MariaDB;
import com.storecrud.models.Category;

public class CategoryController {
    private Vector<Category> Categories;

    // get timestamp

    public static String getCurrentFormattedTimeStamp() {
        // ดึงเวลาปัจจุบัน
        LocalDateTime now = LocalDateTime.now();
        // ตรวจสอบว่าปีเกิน 2500 หรือไม่ ซึ่งแสดงว่าเป็นปี พ.ศ.
        if (now.getYear() > 2500) {
            // แปลงปีพุทธศักราชเป็นคริสต์ศักราชโดยลบ 543 ปี
            now = now.withYear(now.getYear() - 543);
        }
        // ตัด millisecond ออกโดยตั้งค่า nanosecond เป็น 0
        now = now.withNano(0);
        // แปลง LocalDateTime เป็นรูปแบบที่ไม่มี millisecond
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);  // คืนค่าเวลาที่จัดรูปแบบแล้วเป็นสตริง
    }

    public CategoryController() {
        Categories = new <Category>Vector();
    }

    public void viewCategories() {
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
        System.out.println(jsonResponse);
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
                try (PreparedStatement addPreparedStatement = conn.prepareStatement(SQL_INSERT,
                        Statement.RETURN_GENERATED_KEYS)) {

                    addPreparedStatement.setString(1, cat.getName());
                    int row = addPreparedStatement.executeUpdate();

                    if (row > 0) {
                        System.out.println("Category added successfully!");

                        // ดึงค่า cat_id ของหมวดหมู่ที่ถูกเพิ่มล่าสุด
                        ResultSet generatedKeys = addPreparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int lastInsertedId = generatedKeys.getInt(1); // ค่าของ cat_id ที่เพิ่งเพิ่ม

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

    public void updateCategory() {
        MariaDB connDb = new MariaDB();
        Connection conn = connDb.getConnection();
    
        try (Scanner sc = new Scanner(System.in)) {
    
            System.out.print("Category ID: ");
            int cat_id = sc.nextInt(); // รับ cat_id
            sc.nextLine(); // กำจัด newline ที่เหลือจาก nextInt
    
            System.out.print("New Category name: ");
            String cat_name = sc.nextLine(); // รับชื่อหมวดหมู่ใหม่
    
            // อัปเดตข้อมูล
            String SQL_UPDATE = "UPDATE `categories` SET `cat_name` = ?, `updated_at` = ? WHERE `cat_id` = ?";
    
            if (conn != null) {
                try (PreparedStatement updatePreparedStatement = conn.prepareStatement(SQL_UPDATE)) {
    
                    // ตั้งค่าพารามิเตอร์
                    updatePreparedStatement.setString(1, cat_name);
                    updatePreparedStatement.setString(2, getCurrentFormattedTimeStamp()); // ใช้ setString แทน setTimestamp
                    updatePreparedStatement.setInt(3, cat_id);
    
                    int row = updatePreparedStatement.executeUpdate();
    
                    if (row > 0) {
                        System.out.println("Category updated successfully!");
    
                        // ดึงข้อมูลหมวดหมู่ที่ถูกอัปเดต
                        String SQL_SELECT = "SELECT `cat_id`, `cat_name` FROM `categories` WHERE cat_id = ?;";
    
                        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
                            preparedStatement.setInt(1, cat_id); // ใช้ cat_id เดิมที่เพิ่งอัปเดต
                            ResultSet rs = preparedStatement.executeQuery();
    
                            if (rs.next()) {
                                int id = rs.getInt("cat_id");
                                String name = rs.getString("cat_name");
    
                                Category updatedCategory = new Category(id, name);
    
                                // สร้าง JSON response
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String jsonResponse = gson.toJson(updatedCategory);
                                System.out.println("Updated category:");
                                System.out.println(jsonResponse);
                                System.out.println("======================================");
                            }
                        }
                    } else {
                        System.out.println("Failed to update category.");
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

    public void deleteCategory(){
        MariaDB connDb = new MariaDB();
        Connection conn = connDb.getConnection();
    
        try (Scanner sc = new Scanner(System.in)) {
    
            System.out.print("Category ID: ");
            int cat_id = sc.nextInt(); // รับ cat_id
    
            // ลบข้อมูล
            String SQL_DELETE = "DELETE FROM `categories` WHERE `cat_id` = ?";
    
            if (conn != null) {
                try (PreparedStatement deletePreparedStatement = conn.prepareStatement(SQL_DELETE)) {
    
                    deletePreparedStatement.setInt(1, cat_id);
    
                    int row = deletePreparedStatement.executeUpdate();
    
                    if (row > 0) {
                        System.out.println("Category deleted successfully!");
                        // ดึงข้อมูลหมวดหมู่ทั้งหมด
                        viewCategories();
    
                        
                    } else {
                        System.out.println("Failed to Delete category.");
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
