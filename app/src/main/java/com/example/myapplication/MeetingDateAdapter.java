package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MeetingDateAdapter extends  RecyclerView.Adapter<MeetingDateViewHolder>{
    Context context;
    List<String> meetingDates;

    public MeetingDateAdapter(Context context, List<String> meetingDates) {
        this.context = context;
        this.meetingDates = meetingDates;
    }

    @NonNull
    @Override
    public MeetingDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meeting_date, parent, false);
        return new MeetingDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingDateViewHolder holder, int position) {
        String meetingDate = meetingDates.get(position);
        holder.meetingDate.setText(meetingDate);
    }

    @Override
    public int getItemCount() {
        return meetingDates.size();
    }
}
