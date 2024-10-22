package com.example.myapplication;

public class MeetingModel {
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String meetingCreator;
    private String meetingUrl;
    private String meetingId;
    public MeetingModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator, String meetingUrl) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
        this.meetingUrl = meetingUrl;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public String getMeetingCreator() {
        return meetingCreator;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public String getMeetingId() {
        return meetingId;
    }
}
