package com.example.myapplication;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmailSearchInputView extends LinearLayout {

    private EditText emailInput;
    private RecyclerView emailSuggestionsList;
    private ChipGroup emailChipGroup;
    private EmailSuggestionsAdapter emailAdapter;
    private List<String> emailList = new ArrayList<>();
    private List<String> selectedEmails = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EmailSearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.email_search_input_view, this, true);

        emailInput = findViewById(R.id.email_input);
        emailSuggestionsList = findViewById(R.id.email_suggestions_list);
        emailChipGroup = findViewById(R.id.email_chip_group);

        // Setup RecyclerView and Adapter
        emailAdapter = new EmailSuggestionsAdapter(emailList, email -> {
            addEmailChip(email);
            emailInput.setText(""); // Clear input after selection
            emailSuggestionsList.setVisibility(GONE); // Hide suggestions
        });
        emailSuggestionsList.setLayoutManager(new LinearLayoutManager(context));
        emailSuggestionsList.setAdapter(emailAdapter);

        // Add TextWatcher to listen to input changes and fetch suggestions
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchEmailSuggestions(s.toString());
                } else {
                    emailSuggestionsList.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Method to add a chip for the selected email
    private void addEmailChip(String email) {
        selectedEmails.add(email);
        Chip chip = new Chip(getContext());
        chip.setText(email);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            emailChipGroup.removeView(chip);
            selectedEmails.remove(email); // Remove email from selected list
        });
        emailChipGroup.addView(chip);
    }

    // Fetch email suggestions from Firebase Firestore
    private void fetchEmailSuggestions(String input) {
        db.collection("users")
                .whereGreaterThanOrEqualTo("email", input)
                .whereLessThanOrEqualTo("email", input + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        emailList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            if (email != null && !selectedEmails.contains(email)) {
                                emailList.add(email); // Add suggestion if not already selected
                            }
                        }
                        if (!emailList.isEmpty()) {
                            emailAdapter.notifyDataSetChanged();
                            emailSuggestionsList.setVisibility(VISIBLE);
                        } else {
                            emailSuggestionsList.setVisibility(GONE);
                        }
                    }
                });
    }

    // Method to get the selected emails
    public List<String> getSelectedEmails() {
        return selectedEmails;
    }
}
