package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeetingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_MEETING = 1;

    private final Context context;
    private final List<Object> items;

    public MeetingAdapter(Context context, List<MeetingItemModel> meetings) {
        this.context = context;
        this.items = organizeItems(meetings);
    }

    private List<Object> organizeItems(List<MeetingItemModel> meetings) {
        List<Object> organizedItems = new ArrayList<>();
        String lastDate = "";

        for (MeetingItemModel meeting : meetings) {
            String currentDate = meeting.getMeetingDate();
            if (!currentDate.equals(lastDate)) {
                organizedItems.add(currentDate);  // Add date as a separate item
                lastDate = currentDate;
            }
            organizedItems.add(meeting);  // Add the meeting item
        }
        return organizedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_DATE;
        } else {
            return VIEW_TYPE_MEETING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_meeting_date, parent, false);
            return new MeetingDateViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_meeting, parent, false);
            return new MeetingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_DATE) {
            String date = (String) items.get(position);
            ((MeetingDateViewHolder) holder).meetingDate.setText(date);
        } else {
            MeetingItemModel meeting = (MeetingItemModel) items.get(position);
            MeetingViewHolder meetingHolder = (MeetingViewHolder) holder;
            meetingHolder.meetingTitle.setText(meeting.getMeetingTitle());
            meetingHolder.meetingTime.setText(meeting.getMeetingTime());
            meetingHolder.meetingCreator.setText(meeting.getMeetingCreator());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
