package com.example.myapplication;

import java.util.List;

public class MeetingModel {
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String meetingCreator;
    private String meetingUrl;
    private String meetingId;
    private List<String> meetingParticipants;

    public MeetingModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator, String meetingId, List<String> meetingParticipants) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
        this.meetingId = meetingId;
        this.meetingParticipants = meetingParticipants;
    }

    public String getMeetingTitle() {
        return meetingTitle;
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

    public void setMeetingCreator(String meetingCreator) {
        this.meetingCreator = meetingCreator;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
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
