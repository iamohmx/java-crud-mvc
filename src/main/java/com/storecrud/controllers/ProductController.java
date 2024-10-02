package com.storecrud.controllers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storecrud.core.MariaDB;
import com.storecrud.models.Product;

public class ProductController {
    private Vector<Product> Products;

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
        return now.format(formatter); // คืนค่าเวลาที่จัดรูปแบบแล้วเป็นสตริง
    }

    public ProductController() {
        Products = new <Product>Vector();
    }

    public void viewProducts() {
        String SQL_SELECT = "SELECT `p`.`pro_id`, `cat`.`cat_id`, `p`.`pro_name`, `p`.`pro_detail`, `p`.`pro_price`, `cat`.`cat_name` "
                +
                "FROM `products` `p` " +
                "INNER JOIN `categories` `cat` ON `p`.`pro_id` = `cat`.`cat_id`;";

        Vector<Product> products = new Vector<>();

        MariaDB conDb = new MariaDB();
        Connection conn = conDb.getConnection();

        if (conn != null) {
            try {

                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("pro_id");
                    int cat_id = rs.getInt("cat_id");
                    String pro_name = rs.getString("pro_name");
                    String pro_detail = rs.getString("pro_detail");
                    BigDecimal pro_price = rs.getBigDecimal("pro_price");
                    String cat_name = rs.getString("cat_name");

                    Product p = new Product(id, cat_id, pro_name, pro_detail, pro_price, cat_name);
                    products.add(p);

                    // pControllers.add(null)
                }
                preparedStatement.close();
                conn.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        // Iterator it = products.iterator();
        // while (it.hasNext()) {
        // Product product = (Product) it.next();

        // }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResponse = gson.toJson(products);
        System.out.println(jsonResponse);
    }

    public void addProduct() {
        String SQL_INSERT = "INSERT INTO `products` (`cat_id`, `pro_name`, `pro_detail`, `pro_price`) VALUES (?, ?, ?, ?);";

        MariaDB connDb = new MariaDB();
        Connection conn = connDb.getConnection();

        if (conn != null) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Enter Category ID: ");
                int cat_id = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                System.out.print("Enter Product Name: ");
                String pro_name = scanner.nextLine();

                System.out.print("Enter Product Detail: ");
                String pro_detail = scanner.nextLine();

                System.out.print("Enter Product Price: ");
                BigDecimal pro_price = scanner.nextBigDecimal();

                PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT);
                preparedStatement.setInt(1, cat_id);
                preparedStatement.setString(2, pro_name);
                preparedStatement.setString(3, pro_detail);
                preparedStatement.setBigDecimal(4, pro_price);

                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("Product added successfully!");
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
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
