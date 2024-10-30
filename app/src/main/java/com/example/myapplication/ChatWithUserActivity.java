package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatWithUserActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private Button sendButton;
    private TextView messageEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button on the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Retrieve the user ID from the intent
        String userId = getIntent().getStringExtra("USER_ID");
        String username = getIntent().getStringExtra("USERNAME");

        // Display the username
        TextView chatWithTextView = findViewById(R.id.chat_with_username);
        chatWithTextView.setText("Chatting with: " + username);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUserId);

        // Set up RecyclerView
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        // Log userId for debugging
        Log.d("ChatWithUserActivity", "User ID: " + userId);

        // Fetch messages from Firestore
        fetchMessages(userId);

        sendButton = findViewById(R.id.send_button);
        messageEditText = findViewById(R.id.message_input);

        // Set onClickListener for the send button
        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(userId, messageText); // Call the sendMessage method
                messageEditText.setText(""); // Clear the input field
            } else {
                Toast.makeText(ChatWithUserActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click event
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMessages(String userId) {
        String chatId = currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;
        Log.d("ChatWithUserActivity", "Fetching messages for chatId: " + chatId);

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatWithUserActivity", "Listen failed.", e);
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        messageList.clear(); // Clear the list before adding new messages
                        for (QueryDocumentSnapshot document : snapshot) {
                            String text = document.getString("messageText");
                            String senderId = document.getString("senderId");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            if (text != null && senderId != null && timestamp != null) {
                                messageList.add(new Message(text, senderId, timestamp));
                            } else {
                                Log.d("ChatWithUserActivity", "Invalid data in document: " + document.getId());
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                        Log.d("ChatWithUserActivity", "Messages fetched successfully: " + messageList.size());
                    } else {
                        Log.d("ChatWithUserActivity", "No messages found for chatId: " + chatId);
                    }
                });
    }

    private void sendMessage(String userId, String messageText) {
        // Create chatId based on user and current user IDs
        String chatId = currentUserId.compareTo(userId) < 0 ? currentUserId + "_" + userId : userId + "_" + currentUserId;

        // Get a reference to the messages subcollection
        DocumentReference chatRef = db.collection("chats").document(chatId);
        HashMap<String, Object> chat = new HashMap<>();
        chat.put("chatId", chatId);
        chatRef.set(chat)
                .addOnSuccessListener(avoid ->{
                    DocumentReference messageRef = chatRef.collection("messages").document();

                    // Prepare the message data
                    Map<String, Object> message = new HashMap<>();
                    message.put("messageText", messageText);
                    message.put("senderId", currentUserId);
                    message.put("timestamp", Timestamp.now());

                    // Add message to Firestore
                    messageRef.set(message)
                            .addOnSuccessListener(aVoid -> {
                                // Only scroll to the latest message if the message was sent successfully
                                Log.d("ChatWithUserActivity", "Message sent successfully: " + messageText);
                                // Optionally, you can fetch the messages again to update the list
                                fetchMessages(userId); // Re-fetch messages to ensure the list is up-to-date
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(ChatWithUserActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                            });
                }); // Create the chat if it doesn't exist
    }
}