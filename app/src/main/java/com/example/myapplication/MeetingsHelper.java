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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingsHelper {
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

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
                    int[] pendingUsernameFetches = {0}; // Track pending username fetches

                    processQueryResults(creatorTask, userId, meetings, listener, pendingUsernameFetches);
                    processQueryResults(participantTask, userId, meetings, listener, pendingUsernameFetches);

                    // Sort and notify listener after all processing is complete
                    waitForUsernameFetchesAndNotify(listener, meetings, pendingUsernameFetches);
                });
    }

    private Date parseDateTime(MeetingItemModel meeting) {
        try {
            return dateFormat.parse(meeting.getMeetingDate() + " " + meeting.getMeetingTime());
        } catch (ParseException e) {
            Log.e("MeetingsHelper", "Error parsing date and time", e);
            return new Date(); // Return current date if parsing fails
        }
    }

    private void processQueryResults(Task<QuerySnapshot> task, String userId, List<MeetingItemModel> meetings, OnMeetingsFetchedListener listener, int[] pendingUsernameFetches) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                MeetingItemModel meeting = new MeetingItemModel(
                        document.getString("title"),
                        document.getString("date"),
                        document.getString("time"),
                        document.getString("userId"),
                        isCreator(userId, document.getString("userId"))
                );
                meeting.setMeetingId(document.getString("meetingId"));

                meeting.setParticipants_emails((List<String>) document.get("participants_emails"));

                if (meeting.isCreator()) {
                    meeting.setMeetingOwner("You");
                    meetings.add(meeting);
                } else {
                    pendingUsernameFetches[0]++;
                    getUsername(document.getString("userId"), username -> {
                        meeting.setMeetingOwner(username != null ? username : "Unknown");
                        meetings.add(meeting);
                        pendingUsernameFetches[0]--;

                        // Check if all username fetches are complete
                        waitForUsernameFetchesAndNotify(listener, meetings, pendingUsernameFetches);
                    });
                }
            }
        } else {
            Log.d("MeetingsHelper", "Error getting documents: ", task.getException());
        }
    }

    private void waitForUsernameFetchesAndNotify(OnMeetingsFetchedListener listener, List<MeetingItemModel> meetings, int[] pendingUsernameFetches) {
        if (pendingUsernameFetches[0] == 0) { // All username fetches complete
            // Sort the meetings by date and time
            Collections.sort(meetings, Comparator.comparing(this::parseDateTime));
            listener.onMeetingsFetched(meetings);
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
    public void getUsernameEmail(String userEmail, OnUsernameFetchedListener listener) {
        if (userEmail == null) {
            listener.onUsernameFetched(null); // Return early if userId is null
            return;
        }
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (userEmail.equals(document.getString("email"))) {
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

    public void updateMeeting(MeetingItemModel meeting, OnMeetingUpdatedListener listener) {
        database.collection("meetings")
                .whereEqualTo("meetingId", meeting.getMeetingId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().update("title", meeting.getMeetingTitle());
                            document.getReference().update("date", meeting.getMeetingDate());
                            document.getReference().update("time", meeting.getMeetingTime());
                            listener.onMeetingUpdated(true);
                            return;
                        }
                    }
                    listener.onMeetingUpdated(false);
                });
    }
    public interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }
    public interface OnMeetingUpdatedListener {
        void onMeetingUpdated(boolean success);
    }
}
