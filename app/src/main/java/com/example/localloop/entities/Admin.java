package com.example.localloop.entities;

public class Admin extends User {

    public Admin() {
        // Required for Firebase
    }

    public Admin(String name, String role) {
        super(name, role);
    }
}
