package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingViewHolder> {

    Context context;
    List<MeetingItemModel> meetings;

    public MeetingAdapter(Context context, List<MeetingItemModel> meetings) {
        this.context = context;
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.item_meeting, parent, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        MeetingItemModel meeting = meetings.get(position);
        holder.meetingTitle.setText(meeting.getMeetingTitle());
        holder.meetingTime.setText(meeting.getMeetingTime());
        holder.meetingCreator.setText(meeting.getMeetingCreator());
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }
}
