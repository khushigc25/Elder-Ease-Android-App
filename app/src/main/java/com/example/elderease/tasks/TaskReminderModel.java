package com.example.elderease.tasks;

public class TaskReminderModel {
    private String id;
    private String title;
    private long timestamp; // Store time in milliseconds

    public TaskReminderModel() {
        // Empty constructor required for Firebase
    }

    public TaskReminderModel(String id, String title, long timestamp) {
        this.id = id;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
