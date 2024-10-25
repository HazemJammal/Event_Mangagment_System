package com.example.myapplication.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.EmailSuggestions;
import com.example.myapplication.MeetingModel;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMeetingActivity extends AppCompatActivity {
    private EditText etMeetingTitle, etMeetingDate, etMeetingTime;
    private Button btnAddMeeting, btnCancelMeeting;
    private MultiAutoCompleteTextView etMeetingParticipants;
    private ArrayList<String> usersEmails = new ArrayList<>();
    private ArrayList<String> filteredEmails = new ArrayList<>();
    private ArrayAdapter<String> emailAdapter;
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_meeting);

        initializeViews();
        setupEmailSuggestions();
        setupDateAndTimePickers();
        setupButtons();
    }

    private void initializeViews() {
        etMeetingTitle = findViewById(R.id.et_meeting_title);
        etMeetingDate = findViewById(R.id.et_meeting_date);
        etMeetingTime = findViewById(R.id.et_meeting_time);
        btnAddMeeting = findViewById(R.id.btn_add_meeting);
        btnCancelMeeting = findViewById(R.id.btn_cancel_meeting);
        etMeetingParticipants = findViewById(R.id.et_meeting_participants);
        chipGroup = findViewById(R.id.chipGroup);
    }

    private void setupEmailSuggestions() {
        EmailSuggestions emailSuggestions = new EmailSuggestions();
        emailSuggestions.getEmails(emails -> {
            usersEmails.addAll(emails);
            filteredEmails.addAll(emails);
            emailAdapter = new ArrayAdapter<>(AddMeetingActivity.this,
                    android.R.layout.simple_dropdown_item_1line, filteredEmails);
            etMeetingParticipants.setAdapter(emailAdapter);
            etMeetingParticipants.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        });

        etMeetingParticipants.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEmail = (String) parent.getItemAtPosition(position);
            addChipForEmail(selectedEmail);
        });
    }

    private void addChipForEmail(String email) {
        filteredEmails.remove(email);
        updateEmailAdapter();

        Chip chip = new Chip(this);
        chip.setText(email);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            filteredEmails.add(email);
            updateEmailAdapter();
        });

        chipGroup.addView(chip);
        etMeetingParticipants.setText("");
    }

    private void updateEmailAdapter() {
        emailAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, filteredEmails);
        etMeetingParticipants.setAdapter(emailAdapter);
        etMeetingParticipants.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    private void setupDateAndTimePickers() {
        etMeetingDate.setOnClickListener(v -> showDatePickerDialog());
        etMeetingTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void showDatePickerDialog() {    MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Meeting Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build())
            .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            etMeetingDate.setText(date);
        });    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) ->
                        etMeetingTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute)),
                hour, minute, true);

        timePickerDialog.show();
    }

    private void setupButtons() {
        btnAddMeeting.setOnClickListener(v -> saveMeeting());
        btnCancelMeeting.setOnClickListener(v -> finish());
    }

    private void saveMeeting() {
        String title = etMeetingTitle.getText().toString().trim();
        String date = etMeetingDate.getText().toString().trim();
        String time = etMeetingTime.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = mAuth.getCurrentUser().getUid();

            MeetingModel model = new MeetingModel(title, date, time, userId, "test123");
            Map<String, Object> meeting = new HashMap<>();
            meeting.put("title", model.getMeetingTitle());
            meeting.put("date", model.getMeetingDate());
            meeting.put("time", model.getMeetingTime());
            meeting.put("userId", model.getMeetingCreator());
            meeting.put("participants_emails", getParticipantsEmails());

            db.collection("meetings")
                    .add(meeting)
                    .addOnSuccessListener(documentReference -> {
                        String meetingId = documentReference.getId();
                        model.setMeetingId(meetingId);
                        documentReference.update("meetingId", meetingId);
                        Toast.makeText(this, "Meeting Added Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving meeting", Toast.LENGTH_SHORT).show());
        }
    }

    private ArrayList<String> getParticipantsEmails() {
        ArrayList<String> participantsEmails = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            participantsEmails.add(chip.getText().toString());
        }
        return participantsEmails;
    }
}