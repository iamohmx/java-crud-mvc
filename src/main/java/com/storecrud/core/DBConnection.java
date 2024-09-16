package com.storecrud.core;

public class DBConnection {
    String server, username, password;

    DBConnection(String server, String username, String password){
        this.server = server;
        this.username = username;
        this.password = password; 
    }
}
