package com.example.localloop.entities;

public class Participant extends User {

    public Participant() {
        // Required for Firebase
    }

    public Participant(String name) {
        super(name, "Participant");
    }
}
