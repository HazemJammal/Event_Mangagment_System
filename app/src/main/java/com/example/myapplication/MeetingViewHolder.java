package com.example.myapplication;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingViewHolder extends RecyclerView.ViewHolder {

    TextView meetingTime;
    TextView meetingTitle;
    TextView meetingCreator;
    public MeetingViewHolder(@NonNull View itemView) {
        super(itemView);
        meetingTime = itemView.findViewById(R.id.meeting_time);
        meetingTitle = itemView.findViewById(R.id.meeting_title);
        meetingCreator = itemView.findViewById(R.id.meeting_creator);
    }
}
