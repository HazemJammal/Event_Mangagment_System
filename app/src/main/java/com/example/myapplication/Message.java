package com.example.myapplication;

import com.google.firebase.Timestamp;

public class Message {
    private String text;
    private String userId;
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Message(String text, String userId, Timestamp timestamp) {
        this.text = text;
        this.userId = userId;
        this.timestamp = timestamp;
    }


    // Getters
    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }
}
