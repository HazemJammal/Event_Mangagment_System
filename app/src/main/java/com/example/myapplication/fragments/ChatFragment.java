package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ChatWithUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.UserAdapter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private AutoCompleteTextView usernameSearch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> usernameList;
    private ArrayList<String> userIdList = new ArrayList<>();
    private ArrayList<com.example.myapplication.User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    BiMap<String, String> userMap = HashBiMap.create();

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        usernameSearch = view.findViewById(R.id.username_search);
        usernameList = new ArrayList<>();
        userIdList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, usernameList);
        usernameSearch.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetchUsernames(() -> populateChats(userList -> {
            UserAdapter userAdapter = new UserAdapter(userList, requireContext());
            recyclerView.setAdapter(userAdapter);
        }));

        usernameSearch.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedUsername = adapter.getItem(position);
            String selectedUserId = userMap.inverse().get(selectedUsername);

            Intent intent = new Intent(requireContext(), ChatWithUserActivity.class);
            intent.putExtra("USER_ID", selectedUserId);
            intent.putExtra("USERNAME", selectedUsername);
            startActivity(intent);
        });

        return view;
    }

    private void fetchUsernames(Runnable onComplete) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String userId = document.getString("userId");
                            if (username != null && userId != null) {
                                if (!userId.equals(currentUserId)) {
                                    usernameList.add(username);
                                    userIdList.add(userId);
                                    userMap.put(userId, username);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        onComplete.run();
                    } else {
                        Toast.makeText(requireContext(), "Error getting usernames", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void populateChats(OnChatsFetchedListener listener) {
        CollectionReference chatsRef = db.collection("chats");
        chatsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> fetchedUserList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String chatId = document.getString("chatId");

                    if (chatId != null && chatId.contains(currentUserId)) {
                        String otherUserId = chatId.replace(currentUserId, "").replace("_", "");
                        String username = userMap.get(otherUserId);

                        if (username != null) {
                            User user = new User(username, otherUserId);
                            fetchedUserList.add(user);
                        }
                    }
                }

                listener.onchatsfetchedlistener(fetchedUserList);
            } else {
                Log.w("ChatFragment", "Error getting documents.", task.getException());
            }
        });
    }

    public interface OnChatsFetchedListener {
        void onchatsfetchedlistener(List<User> userList);
    }
}