package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MeetingDateViewHolder extends RecyclerView.ViewHolder {

    TextView meetingDate;

    public MeetingDateViewHolder(@NonNull View itemView) {
        super(itemView);
        this.meetingDate = itemView.findViewById(R.id.meeting_date);
    }
}
