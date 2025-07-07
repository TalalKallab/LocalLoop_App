package com.example.localloop.entities;

public class Participant extends User {
    public Participant(String name) {
        super(name, "Participant");
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getRole() {
        return super.getRole();
    }
}
