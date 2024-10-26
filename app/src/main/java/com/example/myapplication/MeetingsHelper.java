package com.example.myapplication;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MeetingsHelper {
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

    public MeetingsHelper() {
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void getMeetings(OnMeetingsFetchedListener listener) {
        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();

// Get a reference to the meetings collection
        CollectionReference meetingRef = database.collection("meetings");

// Create a query to check if the user is the creator
        Query creatorQuery = meetingRef.whereEqualTo("userId", userId);

// Create a query to check if the user is a participant
        Query participantQuery = meetingRef.whereArrayContains("participants_emails", userEmail);

// Execute both queries and combine the results
        Task<QuerySnapshot> creatorTask = creatorQuery.get();
        Task<QuerySnapshot> participantTask = participantQuery.get();

        Tasks.whenAllSuccess(creatorTask, participantTask)
                .addOnCompleteListener(task -> {
                    List<MeetingItemModel> meetings = new ArrayList<>();

                    // Check the results of the creator query
                    if (creatorTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : creatorTask.getResult()) {
                            MeetingItemModel meeting = new MeetingItemModel(
                                    document.getString("title"),
                                    document.getString("date"),
                                    document.getString("time"),
                                    document.getString("userId"),
                                    document.getId()
                            );
                            meetings.add(meeting);
                        }
                    } else {
                        Log.d("MeetingsHelper", "Error getting creator documents: ", creatorTask.getException());
                    }

                    // Check the results of the participant query
                    if (participantTask.isSuccessful()) {
                        for (QueryDocumentSnapshot document : participantTask.getResult()) {
                            MeetingItemModel meeting = new MeetingItemModel(
                                    document.getString("title"),
                                    document.getString("date"),
                                    document.getString("time"),
                                    document.getString("userId"),
                                    document.getId()
                            );
                            // Add only if not already in the list to avoid duplicates
                            if (!meetings.contains(meeting)) {
                                meetings.add(meeting);
                            }
                        }
                    } else {
                        Log.d("MeetingsHelper", "Error getting participant documents: ", participantTask.getException());
                    }

                    // Notify the listener with combined meetings
                    listener.onMeetingsFetched(meetings);
                });
    }

    public interface OnMeetingsFetchedListener {
        void onMeetingsFetched(List<MeetingItemModel> meetings);
    }
}
