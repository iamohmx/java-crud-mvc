package com.storecrud;

import com.storecrud.controllers.CategoryController;
import com.storecrud.controllers.ProductController;

public class Main {
    public static void main(String[] args) {
        // ProductController p = new ProductController();
        // System.out.println(p.viewProducts());
        CategoryController c = new CategoryController();
        c.addCategory();
        // System.out.println(c.viewCategories());

        // System.out.println(getCurrentTimeStamp());


    }

    // public static java.sql.Timestamp getCurrentTimeStamp(){
    //     java.util.Date toDay = new java.util.Date();

    //     return new java.sql.Timestamp(toDay.getTime());
    // }
}