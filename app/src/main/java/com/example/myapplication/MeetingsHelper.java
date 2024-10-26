package com.example.myapplication;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MeetingsHelper {
    private FirebaseFirestore database;

    public MeetingsHelper() {
        database = FirebaseFirestore.getInstance();
    }

    public void getMeeting(MeetingsHelper.OnEmailsFetchedListener listener) {
//        database.collection("meetings")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        MeetingModel meeting;
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            List<String> participants = (List<String>) document.get("participants_emails");
//                            if (participants == null) {
//                                participants = new ArrayList<>();
//                            }
//                            meeting = new MeetingModel(
//                                    document.getString("meetingTitle"),
//                                    document.getString("meetingDate"),
//                                    document.getString("meetingTime"),
//                                    document.getString("meetingCreator"),
//                                    document.getString("meetingId"),
//                                    participants
//                            );
//
//
//                        }
//                        // Notify the listener that emails are fetched
//                        listener.onEmailsFetched(emails);
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//                });
    }

    public interface OnEmailsFetchedListener {
        void onEmailsFetched(List<String> emails);
    }
}
