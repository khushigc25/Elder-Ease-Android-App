package com.example.elderease.sos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.example.elderease.main.AddEmergencyContactsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayEmergencyContactsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EmergencyContactsAdapter adapter;
    private List<EmergencyContactModel> contactList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button addNewEmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_emergency_contacts);

        addNewEmer = findViewById(R.id.addNewEmer);
        recyclerView = findViewById(R.id.emergencyContactsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList = new ArrayList<>();
        adapter = new EmergencyContactsAdapter(this, contactList);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadEmergencyContacts();

        addNewEmer.setOnClickListener(v -> {
            startActivity(new Intent(DisplayEmergencyContactsActivity.this, AddEmergencyContactsActivity.class));
        });
    }

    private void loadEmergencyContacts() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("User").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            List<Map<String, Object>> emergencyContacts = (List<Map<String, Object>>) document.get("emergency_contacts");

                            contactList.clear();
                            if (emergencyContacts != null) {
                                for (Map<String, Object> contactMap : emergencyContacts) {
                                    String name = contactMap.get("name") != null ? contactMap.get("name").toString() : "";
                                    String number = contactMap.get("number") != null ? contactMap.get("number").toString() : "";

                                    if (!name.isEmpty() && !number.isEmpty()) {
                                        contactList.add(new EmergencyContactModel(name, number));
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(DisplayEmergencyContactsActivity.this, "No emergency contacts found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error getting user document", e);
                        Toast.makeText(DisplayEmergencyContactsActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
