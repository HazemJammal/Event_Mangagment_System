package com.example.myapplication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class EmailSuggestions {

    private static final String TAG = "EmailSuggestions";
    private FirebaseFirestore database;
    private List<String> emails;
    private FirebaseAuth mAuth;
    private String userEmail;

    public EmailSuggestions() {
        database = FirebaseFirestore.getInstance();
        emails = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
    }

    public void getEmails(OnEmailsFetchedListener listener) {
        userEmail = mAuth.getCurrentUser().getEmail();
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(!document.getString("email").equals(userEmail)) {
                                emails.add(document.getString("email"));
                            }
                        }
                        // Notify the listener that emails are fetched
                        listener.onEmailsFetched(emails);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public interface OnEmailsFetchedListener {
        void onEmailsFetched(List<String> emails);
    }
}