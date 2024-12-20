package com.example.myapplication;

import java.util.List;

public class MeetingItemModel {
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String meetingCreator; // Creator's ID or name
    private boolean isCreator;
    private String meetingOwner;
    private List<String> participants_emails;
    private String meetingId;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public List<String> getParticipants_emails() {
        return participants_emails;
    }

    public void setParticipants_emails(List<String> participants_emails) {
        this.participants_emails = participants_emails;
    }

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



    public MeetingItemModel() {}

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

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }
}
