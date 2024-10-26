package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingViewHolder extends RecyclerView.ViewHolder {

    TextView meetingTime;
    TextView meetingTitle;
    TextView meetingCreator;
    TextView meetingType; // New TextView for invited/creator status
    View sideBar; // New View for the side bar
    public MeetingViewHolder(@NonNull View itemView) {
        super(itemView);
        meetingTime = itemView.findViewById(R.id.meeting_time);
        meetingTitle = itemView.findViewById(R.id.meeting_title);
        meetingCreator = itemView.findViewById(R.id.meeting_creator);
        meetingType = itemView.findViewById(R.id.meeting_type);
        sideBar = itemView.findViewById(R.id.side_bar);
        // Initialize the new TextView
    }
}
