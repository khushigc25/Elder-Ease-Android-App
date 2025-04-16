package com.example.elderease.medicinesreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elderease.R;
import com.example.elderease.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MedicinesReminderActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String userId;
    private DocumentReference userRef;

    private EditText breakfastTime, lunchTime, dinnerTime;
    Button editMealTimeBtn, addNewMedBtn;

    // RecyclerView for medicine reminders
    private RecyclerView recyclerView;
    private MedicineReminderAdapter adapter;
    private TextView noRemindersText;
    private List<MedicineReminderModel> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines_reminder);

        // Meal time and button initialization
        editMealTimeBtn = findViewById(R.id.btn_edit_meal_time);
        editMealTimeBtn.setOnClickListener(v ->
                startActivity(new Intent(MedicinesReminderActivity.this, EditMealTimeActivity.class))
        );

        addNewMedBtn = findViewById(R.id.btn_add_new_med);
        addNewMedBtn.setOnClickListener(v ->
                startActivity(new Intent(MedicinesReminderActivity.this, AddNewMedReminderActivity.class))
        );

        Button infoButton = findViewById(R.id.infoButton);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            userRef = db.collection("User").document(userId);
            // Check if meal times are set; if not, show the popup
            checkMealTimes();
        }

        // Initialize RecyclerView to display medicine reminders
        noRemindersText = findViewById(R.id.tv_no_reminders);
        recyclerView = findViewById(R.id.rv_medicine_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicineReminderAdapter(this, reminderList);
        recyclerView.setAdapter(adapter);

        // Fetch reminders from the Firebase Realtime Database
        fetchReminders();
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📌 Medicine Reminder Info")
                .setMessage(getFormattedMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private Spanned getFormattedMessage() {
        return Html.fromHtml(
                "<b><font color='#1E88E5'>How Your Medicine Reminders Work:</font></b><br><br>" +
                        "<font color='#757575'>Your reminders will ring based on your selected meal times:</font><br><br>" +
                        "<b>🕒 Before & After Meals:</b><br>" +
                        "• <b>Before Breakfast</b> → <font color='#388E3C'>15 mins before</font><br>" +
                        "• <b>After Breakfast</b> → <font color='#388E3C'>45 mins after</font><br>" +
                        "• <b>Before Lunch</b> → <font color='#388E3C'>15 mins before</font><br>" +
                        "• <b>After Lunch</b> → <font color='#388E3C'>45 mins after</font><br>" +
                        "• <b>Before Dinner</b> → <font color='#388E3C'>15 mins before</font><br>" +
                        "• <b>After Dinner</b> → <font color='#388E3C'>45 mins after</font><br><br>" +
                        "📅 <b>Custom Days:</b> You can also set reminders for <b>Saturday & Sunday</b> separately."
        );
    }

    private void checkMealTimes() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists() &&
                        document.contains("breakfastTime") &&
                        document.contains("lunchTime") &&
                        document.contains("dinnerTime")) {
                    Log.d("Firestore", "Meal times already set. Skipping popup.");
                } else {
                    showMealTimePopup();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMealTimePopup() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Your Meal Times");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 20, 50, 20);

            TextView instructionText = new TextView(this);
            instructionText.setText("Please enter your meal times for better medicine reminders.");
            instructionText.setPadding(10, 10, 10, 20);
            instructionText.setTextSize(16);

            breakfastTime = new EditText(this);
            lunchTime = new EditText(this);
            dinnerTime = new EditText(this);

            breakfastTime.setHint("Breakfast Time");
            lunchTime.setHint("Lunch Time");
            dinnerTime.setHint("Dinner Time");

            // Make fields non-editable so the time picker is used
            breakfastTime.setFocusable(false);
            lunchTime.setFocusable(false);
            dinnerTime.setFocusable(false);

            breakfastTime.setOnClickListener(v -> showTimePickerDialog(breakfastTime));
            lunchTime.setOnClickListener(v -> showTimePickerDialog(lunchTime));
            dinnerTime.setOnClickListener(v -> showTimePickerDialog(dinnerTime));

            layout.addView(instructionText);
            layout.addView(breakfastTime);
            layout.addView(lunchTime);
            layout.addView(dinnerTime);

            builder.setView(layout);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();

            // Add Save Button Programmatically
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (d, which) -> { });

            dialog.setOnShowListener(d -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> saveMealTimes(dialog));

                // Enable save button when all fields have text
                MealTimeTextWatcher watcher = new MealTimeTextWatcher(dialog, breakfastTime, lunchTime, dinnerTime);
                breakfastTime.addTextChangedListener(watcher);
                lunchTime.addTextChangedListener(watcher);
                dinnerTime.addTextChangedListener(watcher);
            });

            if (!isFinishing() && !isDestroyed()) {
                dialog.show();
            }
        });
    }

    private void showTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Convert 24-hour format to 12-hour format with AM/PM
                    String amPm = (selectedHour >= 12) ? "PM" : "AM";
                    int hour12Format = (selectedHour == 0 || selectedHour == 12) ? 12 : selectedHour % 12;
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour12Format, selectedMinute, amPm);
                    editText.setText(formattedTime);
                }, hour, minute, false); // false for 12-hour format

        timePickerDialog.show();
    }

    private void saveMealTimes(AlertDialog dialog) {
        String breakfast = breakfastTime.getText().toString().trim();
        String lunch = lunchTime.getText().toString().trim();
        String dinner = dinnerTime.getText().toString().trim();

        if (breakfast.isEmpty() || lunch.isEmpty() || dinner.isEmpty()) {
            Toast.makeText(this, "Please select all meal times", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mealTimes = new HashMap<>();
        mealTimes.put("breakfastTime", breakfast);
        mealTimes.put("lunchTime", lunch);
        mealTimes.put("dinnerTime", dinner);

        userRef.set(mealTimes, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Meal times saved successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save meal times", Toast.LENGTH_SHORT).show());
    }

    private void fetchReminders() {
        DatabaseReference remindersRef = FirebaseDatabase.getInstance().getReference("MedicineReminders").child(userId);

        remindersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                        MedicineReminderModel reminder = reminderSnapshot.getValue(MedicineReminderModel.class);
                        reminderList.add(reminder);
                    }
                    noRemindersText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    noRemindersText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MedicinesReminderActivity.this, "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
