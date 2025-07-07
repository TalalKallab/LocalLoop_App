package com.example.localloop;

public class Event {
    private String title;
    private String description;
    private String category;
    private String dateAndTime;
    private String location;
    private double fee;
    private String id;
    private final int MAX_PARTICIPANTS;

    // No-argument constructor for Firebase
    public Event() {
        this.MAX_PARTICIPANTS = 0;
    }

    public Event(String title, String description, String category, String dateAndTime,
                 String location, int maxParticipants, double fee, String id) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.dateAndTime = dateAndTime;
        this.location = location;
        this.fee = fee;
        this.id = id;
        this.MAX_PARTICIPANTS = maxParticipants;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getLocation() {
        return location;
    }

    public double getFee() {
        return fee;
    }

    public String getId() {
        return id;
    }

    public int getMAX_PARTICIPANTS() {
        return MAX_PARTICIPANTS;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Placeholder methods for future extension
    public void addParticipant() {
        // Implementation for participant management can go here
    }

    public void pastEvents() {
        // Implementation for retrieving past events can go here
    }
}
