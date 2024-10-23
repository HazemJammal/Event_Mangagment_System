package com.example.myapplication.fragments;

import static java.security.AccessController.getContext;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.MeetingModel;
import com.example.myapplication.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMeetingActivity extends AppCompatActivity {
    private EditText etMeetingTitle, etMeetingDate, etMeetingTime;
    private Button btnAddMeeting, btnCancelMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_meeting);

        etMeetingTitle = findViewById(R.id.et_meeting_title);
        etMeetingDate = findViewById(R.id.et_meeting_date);
        etMeetingTime = findViewById(R.id.et_meeting_time);
        btnAddMeeting = findViewById(R.id.btn_add_meeting);
        btnCancelMeeting = findViewById(R.id.btn_cancel_meeting);

        // Set the date picker listener for the meeting date EditText
        etMeetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        etMeetingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour in 24-hour format
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddMeetingActivity.this, // Use getContext() or getActivity()
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int selectedHour, int selectedMinute) {
                                // Format the selected hour and minute to HH:mm
                                etMeetingTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                            }
                        },
                        hour, minute,
                        true // Set this to true for 24-hour format
                );

                timePickerDialog.show();
            }
        });
        // Save button logic
        btnAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String title = etMeetingTitle.getText().toString().trim();
                String date = etMeetingDate.getText().toString().trim();
                String time = etMeetingTime.getText().toString().trim();

                if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(AddMeetingActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle saving the meeting details (e.g., send the result back or save in a database)
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = mAuth.getCurrentUser().getUid();


                    MeetingModel model = new MeetingModel(title, date, time,userId,"test123");

                    Map<String, Object> meeting = new HashMap<>();
                    meeting.put("title", model.getMeetingTitle());
                    meeting.put("date", model.getMeetingDate());
                    meeting.put("time", model.getMeetingTime());
                    meeting.put("userId", model.getMeetingCreator());

                    db.collection("meetings")
                            .add(meeting)
                            .addOnSuccessListener(documentReference -> {
                                String meetingId = documentReference.getId();
                                model.setMeetingId(meetingId);
                                documentReference.update("meetingId",meetingId);

                                Toast.makeText(AddMeetingActivity.this, "Meeting Added Successfully", Toast.LENGTH_SHORT).show();
                                // Close the activity after saving
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddMeetingActivity.this, "Error saving meeting", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        // Cancel button logic
        btnCancelMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity and go back to the home fragment


                finish();
            }
        });
    }

    private void addMeetingToDb(MeetingModel model){

    }

    private void showDatePickerDialog() {
        // Create a MaterialDatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Meeting Date");

        // Create the date picker
        MaterialDatePicker<Long> datePicker = builder.build();

        // Show the date picker
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        // Set the selection listener
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Format the selected date
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String date = calendar.get(Calendar.YEAR) + "-" +
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" +
                    String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
            etMeetingDate.setText(date); // Set the selected date to EditText
        });
}
}
