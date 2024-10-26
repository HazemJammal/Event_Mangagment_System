package com.example.myapplication;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        CollectionReference meetingRef = database.collection("meetings");
        Query creatorQuery = meetingRef.whereEqualTo("userId", userId);
        Query participantQuery = meetingRef.whereArrayContains("participants_emails", userEmail);

        Task<QuerySnapshot> creatorTask = creatorQuery.get();
        Task<QuerySnapshot> participantTask = participantQuery.get();

        Tasks.whenAllSuccess(creatorTask, participantTask)
                .addOnCompleteListener(task -> {
                    List<MeetingItemModel> meetings = new ArrayList<>();
                    processQueryResults(creatorTask, userId, meetings, listener);
                    processQueryResults(participantTask, userId, meetings, listener);
                });
    }

    private void processQueryResults(Task<QuerySnapshot> task, String userId, List<MeetingItemModel> meetings, OnMeetingsFetchedListener listener) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                MeetingItemModel meeting = new MeetingItemModel(
                        document.getString("title"),
                        document.getString("date"),
                        document.getString("time"),
                        document.getString("userId"),
                        isCreator(userId, document.getString("userId"))
                );

                // Set meeting owner if the creator
                if (meeting.isCreator()) {
                    meeting.setMeetingOwner("You");
                } else {
                    // Retrieve username asynchronously
                    getUsername(document.getString("userId"), new OnUsernameFetchedListener() {
                        @Override
                        public void onUsernameFetched(String username) {
                            if (username != null) {
                                meeting.setMeetingOwner(username);
                            } else {
                                meeting.setMeetingOwner("Unknown");
                            }
                            // Add meeting after setting the owner
                            if (!meetings.contains(meeting)) {
                                meetings.add(meeting);
                            }
                            // Notify listener after all usernames are fetched
                            listener.onMeetingsFetched(meetings);
                        }
                    });
                    continue; // Skip adding meeting immediately
                }

                // Add meeting directly if the user is the creator
                if (!meetings.contains(meeting)) {
                    meetings.add(meeting);
                }
            }
            // Notify listener if all meetings were fetched without needing username
            if (meetings.size() > 0) {
                listener.onMeetingsFetched(meetings);
            }
        } else {
            Log.d("MeetingsHelper", "Error getting documents: ", task.getException());
            listener.onMeetingsFetched(new ArrayList<>()); // Notify with an empty list on failure
        }
    }

    private boolean isCreator(String userId, String creatorId) {
        return userId.equals(creatorId);
    }

    public interface OnMeetingsFetchedListener {
        void onMeetingsFetched(List<MeetingItemModel> meetings);
    }

    public void getUsername(String userId, OnUsernameFetchedListener listener) {
        if (userId == null) {
            listener.onUsernameFetched(null); // Return early if userId is null
            return;
        }
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (userId.equals(document.getString("userId"))) {
                                listener.onUsernameFetched(document.getString("username"));
                                return;
                            }
                        }
                        // If no matching userId is found, return null
                        listener.onUsernameFetched(null);
                    } else {
                        // Handle task failure case
                        Log.e("FirestoreError", "Error fetching username: ", task.getException());
                        listener.onUsernameFetched(null);
                    }
                });
    }

    public interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }
}
