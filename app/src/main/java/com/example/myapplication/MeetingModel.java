package com.example.myapplication;

import java.util.List;

public class MeetingModel {
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String meetingCreator;
    private String meetingId;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public MeetingModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
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


    public String getMeetingDate() {
        return meetingDate;
    }


    public String getMeetingTime() {
        return meetingTime;
    }

    public String getMeetingCreator() {
        return meetingCreator;
    }


}
