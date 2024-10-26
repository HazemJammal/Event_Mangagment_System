package com.example.myapplication;

public class MeetingItemModel {
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String meetingCreator; // Creator's ID or name
    private boolean isCreator;
    private String meetingOwner;

    public String getMeetingOwner() {
        return meetingOwner;
    }

    public void setMeetingOwner(String meetingOwner) {
        this.meetingOwner = meetingOwner;
    }
// Indicates if the current user is the creator
    // Add other fields as needed

    // Constructor
    public MeetingItemModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator, boolean isCreator) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
        this.isCreator = isCreator;
    }

    // Getters
    public String getMeetingTitle() {
        return meetingTitle;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public String getUserId() {
        return meetingCreator; // or however you identify the creator
    }

    public boolean isCreator() {
        return isCreator;
    }
}
