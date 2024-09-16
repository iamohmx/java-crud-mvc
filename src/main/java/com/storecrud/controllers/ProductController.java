package com.storecrud.controllers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.storecrud.core.MariaDB;
import com.storecrud.models.Product;

public class ProductController {
    private Vector<Product> Products;

    public ProductController () {
        Products = new<Product> Vector();
    }

    public String viewProducts() {
        String SQL_SELECT = "SELECT `p`.`pro_id`, `cat`.`cat_id`, `p`.`pro_name`, `p`.`pro_detail`, `p`.`pro_price`, `cat`.`cat_name` " + 
                            "FROM `products` `p` " + 
                            "INNER JOIN `categories` `cat` ON `p`.`pro_id` = `cat`.`cat_id`;";

        Vector<Product> products = new Vector<>();

        MariaDB conDb = new MariaDB();
        Connection conn = conDb.getConnection();
        
        if (conn != null) {
            try {
                 
                PreparedStatement preparedStatement =  conn.prepareStatement(SQL_SELECT);
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
        //     Product product = (Product) it.next();
            
        // }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResponse = gson.toJson(products);
        return jsonResponse; 
    }   
}
