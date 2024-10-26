package com.example.myapplication;

import java.util.List;

public class MeetingItemModel {
     String meetingTitle;
     String meetingDate;
     String meetingTime;
     String meetingCreator;
     String meetingId;
     List<String> meetingParticipantsEmails;

    public MeetingItemModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator, String meetingId, List<String> meetingParticipants) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
        this.meetingId = meetingId;
        this.meetingParticipantsEmails = meetingParticipants;
    }
    public MeetingItemModel(String meetingTitle, String meetingDate, String meetingTime, String meetingCreator, String meetingId) {
        this.meetingTitle = meetingTitle;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.meetingCreator = meetingCreator;
        this.meetingId = meetingId;
    }
    public MeetingItemModel(){}
    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingCreator() {
        return meetingCreator;
    }

    public void setMeetingCreator(String meetingCreator) {
        this.meetingCreator = meetingCreator;
    }
}
