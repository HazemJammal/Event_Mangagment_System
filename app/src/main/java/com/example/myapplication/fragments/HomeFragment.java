package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MeetingAdapter;
import com.example.myapplication.MeetingItemModel;
import com.example.myapplication.MeetingsHelper;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        List<MeetingItemModel> meetings = new ArrayList<>();

        MeetingsHelper meetingsHelper = new MeetingsHelper();
        meetingsHelper.getMeetings(fetchedMeetings -> {
            meetings.addAll(fetchedMeetings);
            setupRecyclerView(view, meetings); // Set up RecyclerView here
        });

        setupFloatingActionButton(view);
        return view;
    }
    private void setupRecyclerView(View view, List<MeetingItemModel> meetings) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new MeetingAdapter(this.getContext(), meetings));
    }

    private void setupFloatingActionButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMeetingActivity.class);
            startActivity(intent);
        });
    }

    private List<MeetingItemModel> getMeetings() {
        return new ArrayList<>(Arrays.asList(
                new MeetingItemModel("Project Kickoff", "2024-11-01", "10:00", "Alice", "1",
                        Arrays.asList("bob@example.com", "carol@example.com")),
                new MeetingItemModel("Design Review", "2024-11-01", "14:00", "Bob", "2",
                        Arrays.asList("alice@example.com", "dave@example.com")),
                new MeetingItemModel("Sprint Planning", "2024-11-02", "09:00", "Carol", "3",
                        Arrays.asList("bob@example.com", "alice@example.com")),
                new MeetingItemModel("Client Presentation", "2024-11-02", "11:00", "Dave", "4",
                        Arrays.asList("carol@example.com", "alice@example.com")),
                new MeetingItemModel("Team Retrospective", "2024-11-03", "15:00", "Alice", "5",
                        Arrays.asList("bob@example.com", "dave@example.com")),
                new MeetingItemModel("Code Review", "2024-11-03", "16:00", "Carol", "6",
                        Arrays.asList("alice@example.com", "carol@example.com")),
                new MeetingItemModel("Product Demo", "2024-11-04", "10:00", "Bob", "7",
                        Arrays.asList("alice@example.com", "bob@example.com")),
                new MeetingItemModel("Marketing Sync", "2024-11-05", "13:00", "Dave", "8",
                        Arrays.asList("carol@example.com", "alice@example.com")),
                new MeetingItemModel("Budget Review", "2024-11-05", "15:00", "Alice", "9",
                        Arrays.asList("bob@example.com", "dave@example.com")),
                new MeetingItemModel("Launch Plan", "2024-11-06", "09:00", "Carol", "10",
                        Arrays.asList("dave@example.com", "bob@example.com"))
        ));
    }
}