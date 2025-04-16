package com.example.elderease.sos;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {
    private Context context;
    private List<EmergencyContactModel> contactList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public EmergencyContactsAdapter(Context context, List<EmergencyContactModel> contactList) {
        this.context = context;
        this.contactList = contactList;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContactModel contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.numberTextView.setText(contact.getNumber());

        // Edit Button Click Listener with custom dialog
        holder.editButton.setOnClickListener(v -> showEditDialog(contact, position));

        // Delete Button Click Listener
        holder.deleteButton.setOnClickListener(v -> deleteContact(contact, position));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, numberTextView;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contactName);
            numberTextView = itemView.findViewById(R.id.contactNumber);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Function to show the Edit Contact dialog using a custom layout
    private void showEditDialog(EmergencyContactModel contact, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_contact, null);
        builder.setView(dialogView);

        // Retrieve UI elements from the custom layout
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        final EditText etEditNumber = dialogView.findViewById(R.id.etEditNumber);

        // Optionally update the title dynamically
        tvTitle.setText("Edit Contact Number");
        etEditNumber.setInputType(InputType.TYPE_CLASS_PHONE);
        etEditNumber.setText(contact.getNumber());

        // Set up dialog buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newNumber = etEditNumber.getText().toString().trim();
            if (isValidMobileNumber(newNumber)) {
                contact.setNumber(newNumber);
                updateContactInFirestore(contact);
                notifyItemChanged(position);
                Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Invalid mobile number. Enter a valid 10-digit number.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Function to validate mobile number
    private boolean isValidMobileNumber(String number) {
        return number.matches("^[6-9]\\d{9}$");  // Ensures a 10-digit number starting with 6-9
    }

    // Function to delete a contact from Firestore
    private void deleteContact(EmergencyContactModel contact, int position) {
        if (contactList.size() == 1) {
            Toast.makeText(context, "At least one emergency contact is required", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete " + contact.getName() + " from emergency contacts?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                db.collection("User").document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                List<Map<String, Object>> emergencyContacts =
                                        (List<Map<String, Object>>) documentSnapshot.get("emergency_contacts");
                                if (emergencyContacts != null) {
                                    // Remove the contact from the list
                                    emergencyContacts.remove(position);

                                    // Update Firestore
                                    db.collection("User").document(userId)
                                            .update("emergency_contacts", emergencyContacts)
                                            .addOnSuccessListener(aVoid -> {
                                                contactList.remove(position);
                                                notifyItemRemoved(position);
                                                Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete contact", Toast.LENGTH_SHORT).show());
                                }
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Error fetching contacts", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the confirmation dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // Function to update a contact's number in Firestore
    private void updateContactInFirestore(EmergencyContactModel updatedContact) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("User").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<Map<String, Object>> emergencyContacts =
                                    (List<Map<String, Object>>) documentSnapshot.get("emergency_contacts");
                            if (emergencyContacts != null) {
                                // Update the contact details based on the name
                                for (Map<String, Object> contactMap : emergencyContacts) {
                                    if (contactMap.get("name").equals(updatedContact.getName())) {
                                        contactMap.put("number", updatedContact.getNumber());
                                        break;
                                    }
                                }

                                // Save the updated list in Firestore
                                db.collection("User").document(userId)
                                        .update("emergency_contacts", emergencyContacts)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Contact Updated Successfully", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to update contact", Toast.LENGTH_SHORT).show());
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error updating contact", Toast.LENGTH_SHORT).show());
        }
    }
}
