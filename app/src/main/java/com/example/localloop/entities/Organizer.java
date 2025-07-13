package com.example.localloop.entities;

public class Organizer extends User {

    public Organizer() {
        // Required for Firebase
    }

    public Organizer(String name) {
        super(name, "Organizer");
    }
}
