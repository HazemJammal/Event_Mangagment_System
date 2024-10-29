package com.example.myapplication.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MeetingItemModel;
import com.example.myapplication.MeetingsHelper;
import com.example.myapplication.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class MeetingDetailsFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView titleTextView, ownerTextView, dateTextView, timeTextView;
    private EditText titleEditText, dateEditText, timeEditText;
    private ChipGroup chipGroupParticipants;
    private Button buttonEdit, buttonDelete, backButton;
    private boolean isInEditMode = false;
    private String meetingId;

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
        if (buttonEdit != null) {
            buttonEdit.setOnClickListener(v -> {
                if (isInEditMode) {
                    confirmChanges();
                    isInEditMode = false;
                } else {
                    switchToEdit();
                    isInEditMode = true;
                }
            });
        }
    }

    private void handleBackButton() {
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void populateMeetingDetails(Bundle bundle) {
        if (bundle != null) {
            meetingId = bundle.getString("meetingId");
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

    private void confirmChanges() {
        MeetingsHelper helper = new MeetingsHelper();

        MeetingItemModel meeting = new MeetingItemModel();
        meeting.setMeetingId(meetingId);
        meeting.setMeetingTitle(titleEditText.getText().toString());
        meeting.setMeetingDate(dateEditText.getText().toString());
        meeting.setMeetingTime(timeEditText.getText().toString());

        helper.updateMeeting(meeting, success -> {
            if (success) {
                Toast.makeText(requireContext(), "Meeting updated successfully", Toast.LENGTH_SHORT).show();
                Log.d("MeetingDetailsFragment", "Meeting updated successfully");
            } else {
                Log.e("MeetingDetailsFragment", "Failed to update meeting");
            }
        });

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
    }

    private void switchToEdit() {
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
        isInEditMode = true;

        setupDateAndTimePickers(); // Set up date and time pickers
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, selectedHour, selectedMinute) ->
                        timeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute)),
                hour, minute, true);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Meeting Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            dateEditText.setText(date);
        });
    }

    private void setupDateAndTimePickers() {
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());
    }
}