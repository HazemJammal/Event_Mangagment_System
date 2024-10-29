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
import java.util.List;

public class HomeFragment extends Fragment implements MeetingAdapter.OnMeetingClickListener {

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

        // Fetch meetings and set up the RecyclerView
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Pass 'this' as the listener to the adapter
        MeetingAdapter adapter = new MeetingAdapter(getContext(), meetings, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupFloatingActionButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMeetingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMeetingClick(MeetingItemModel meeting) {
        Bundle bundle = new Bundle();
        bundle.putString("meetingTitle", meeting.getMeetingTitle());
        bundle.putString("meetingId", meeting.getMeetingId());
        bundle.putString("meetingDate", meeting.getMeetingDate());
        bundle.putString("meetingTime", meeting.getMeetingTime());
        bundle.putString("meetingOwner", meeting.getMeetingOwner());
        bundle.putStringArrayList("participants_emails", (ArrayList<String>) meeting.getParticipants_emails());

        MeetingDetailsFragment meetingDetailsFragment = new MeetingDetailsFragment();
        meetingDetailsFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, meetingDetailsFragment)
                .addToBackStack(null)
                .commit();
    }
}
