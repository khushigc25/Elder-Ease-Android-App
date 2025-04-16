package com.example.elderease.main;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.elderease.R;
import com.example.elderease.sos.DisplayEmergencyContactsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEmergencyContactsActivity extends AppCompatActivity {

    private LinearLayout contactsContainer;
    private Button addContactButton, saveContactsButton;
    private List<View> contactViews = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency_contacts);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get logged-in user ID

        contactsContainer = findViewById(R.id.contactsContainer);
        addContactButton = findViewById(R.id.addContactButton);
        saveContactsButton = findViewById(R.id.saveContactsButton);

        // Add the first contact field
        addNewContactField();

        addContactButton.setOnClickListener(v -> addNewContactField());

        saveContactsButton.setOnClickListener(v -> saveContactsToFirestore());
    }

    private void addNewContactField() {
        // Inflate a new contact field layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View contactView = inflater.inflate(R.layout.contact_input_layout, contactsContainer, false);

        contactsContainer.addView(contactView);
        contactViews.add(contactView);
    }

    private void saveContactsToFirestore() {
        List<Map<String, String>> newContactsList = new ArrayList<>();

        for (View view : contactViews) {
            EditText nameField = view.findViewById(R.id.contactName);
            EditText numberField = view.findViewById(R.id.contactNumber);

            String name = nameField.getText().toString().trim();
            String number = numberField.getText().toString().trim();

            // Validate name and number
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhoneNumber(number)) {
                Toast.makeText(this, "Enter a valid 10-digit phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> contact = new HashMap<>();
            contact.put("name", name);
            contact.put("number", number);
            newContactsList.add(contact);
        }

        if (newContactsList.isEmpty()) {
            Toast.makeText(this, "Please enter at least one emergency contact", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("User").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<Map<String, String>> existingContacts = new ArrayList<>();

                    if (documentSnapshot.exists()) {
                        // Retrieve existing contacts if they exist
                        Object contactsObj = documentSnapshot.get("emergency_contacts");
                        if (contactsObj instanceof List) {
                            existingContacts = (List<Map<String, String>>) contactsObj;
                        }
                    }

                    // Append new contacts to the existing list
                    existingContacts.addAll(newContactsList);

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("emergency_contacts", existingContacts);

                    firestore.collection("User").document(userId)
                            .update(updatedData) // Use 'update' to avoid overwriting existing data
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Emergency contacts saved!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddEmergencyContactsActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to save contacts!", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to retrieve existing contacts!", Toast.LENGTH_SHORT).show());
    }


    private boolean isValidPhoneNumber(String number) {
        return number.matches("\\d{10}"); // Ensures only digits and exactly 10 characters
    }
}
