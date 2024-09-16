package com.storecrud.models;

import java.math.BigDecimal;

public class Product {
    private int id, cat_id;
    private String name, detail, cat_name;
    private BigDecimal price;
   
    public Product (int id, int cat_id, String name, String detail, BigDecimal price, String cat_name ){
        this.id = id;
        this.cat_id = cat_id;
        this.name = name;
        this.detail = detail;
        this.price = price;
        this.cat_name = cat_name;
    }
    
    public String toString() {
        Category category = new Category();
        category.setId(cat_id);
        category.setName(cat_name);

        return "id: " + id + ", name: " + name + 
        ", detail: " + detail + ", price: " + price + 
        ", cat_id: " + category.getId() + 
        ", cat_name: " + category.getName();
    }

}
