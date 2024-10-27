package com.example.myapplication.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MeetingsHelper;
import com.example.myapplication.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MeetingDetailsFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView titleTextView, ownerTextView, dateTextView, timeTextView;
    private EditText titleEditText, dateEditText, timeEditText;
    private ChipGroup chipGroupParticipants;
    private Button buttonEdit, buttonDelete, backButton;
    private boolean isInEditMode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_details, container, false);
        setupView(view);
        populateMeetingDetails(getArguments());
        return view;
    }

    private void setupView(View view) {
        titleTextView = view.findViewById(R.id.meeting_title);
        ownerTextView = view.findViewById(R.id.meeting_creator);
        dateTextView = view.findViewById(R.id.meeting_date);
        timeTextView = view.findViewById(R.id.meeting_time);
        chipGroupParticipants = view.findViewById(R.id.chipGroupParticipants);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        backButton = view.findViewById(R.id.backButton);

        handleBackButton();
        handleEditButton();
    }

    private void handleEditButton() {
        buttonEdit.setOnClickListener(v -> editMeetingToggle());
    }

    private void handleBackButton() {
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void populateMeetingDetails(Bundle bundle) {
        if (bundle != null) {
            titleTextView.setText(bundle.getString("meetingTitle"));
            ownerTextView.setText(bundle.getString("meetingOwner"));
            dateTextView.setText(bundle.getString("meetingDate"));
            timeTextView.setText(bundle.getString("meetingTime"));

            setEditAndDeleteButtonsVisibility(bundle.getString("meetingOwner"));
            ArrayList<String> participantsEmails = bundle.getStringArrayList("participants_emails");
            if (participantsEmails != null) {
                addParticipantsChips(chipGroupParticipants, participantsEmails);
            }
        }
    }

    private void setEditAndDeleteButtonsVisibility(String meetingOwner) {
        if (TextUtils.equals(meetingOwner, "You")) {
            buttonEdit.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            buttonEdit.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }
    }

    private void addParticipantsChips(ChipGroup chipGroup, ArrayList<String> emails) {
        String userEmail = mAuth.getCurrentUser().getEmail();
        MeetingsHelper helper = new MeetingsHelper();

        for (String email : emails) {
            Chip chip = new Chip(getContext());
            chip.setCloseIconEnabled(false);
            chip.setClickable(false);
            if (TextUtils.equals(email, userEmail)) {
                chip.setText("You");
            } else {
                helper.getUsernameEmail(email, username -> chip.setText(username != null ? username : email));
            }
            chipGroup.addView(chip);
        }
    }

    private void editMeetingToggle(){
        if (isInEditMode) {
            // Save data and revert to view mode
            titleTextView.setText(titleEditText.getText().toString());
            dateTextView.setText(dateEditText.getText().toString());
            timeTextView.setText(timeEditText.getText().toString());

            titleTextView.setVisibility(View.VISIBLE);
            dateTextView.setVisibility(View.VISIBLE);
            timeTextView.setVisibility(View.VISIBLE);

            titleEditText.setVisibility(View.GONE);
            dateEditText.setVisibility(View.GONE);
            timeEditText.setVisibility(View.GONE);

            buttonEdit.setText("Edit Meeting");
        } else {
            // Switch to edit mode
            titleEditText = new EditText(requireContext());
            dateEditText = new EditText(requireContext());
            timeEditText = new EditText(requireContext());

            titleEditText.setText(titleTextView.getText());
            dateEditText.setText(dateTextView.getText());
            timeEditText.setText(timeTextView.getText());

            titleTextView.setVisibility(View.GONE);
            dateTextView.setVisibility(View.GONE);
            timeTextView.setVisibility(View.GONE);

            ((ViewGroup) titleTextView.getParent()).addView(titleEditText);
            ((ViewGroup) dateTextView.getParent()).addView(dateEditText);
            ((ViewGroup) timeTextView.getParent()).addView(timeEditText);

            buttonEdit.setText("Confirm Changes");
        }
        isInEditMode = !isInEditMode;
    }


}
