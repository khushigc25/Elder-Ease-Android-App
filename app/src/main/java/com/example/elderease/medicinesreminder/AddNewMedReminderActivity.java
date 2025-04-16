package com.example.elderease.medicinesreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewMedReminderActivity extends AppCompatActivity {

    private EditText etMedicineName, etDosage;
    private Spinner spinnerMedicineType;
    private CheckBox checkBeforeBreakfast, checkAfterBreakfast,
            checkBeforeLunch, checkAfterLunch,
            checkBeforeDinner, checkAfterDinner,
            checkEveryday, checkMonday, checkTuesday, checkWednesday,
            checkThursday, checkFriday, checkSaturday, checkSunday;
    private Button btnSaveReminder;
    private DatabaseReference databaseReference;


    // Meal times from Firestore – expected as strings like "09:43 PM" (12-hour format with AM/PM)
    private String breakfastTimeStr, lunchTimeStr, dinnerTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_med_reminder);

        // Initialize UI components
        etMedicineName = findViewById(R.id.et_medicine_name);
        etDosage = findViewById(R.id.et_dosage);

        spinnerMedicineType = findViewById(R.id.spinner_medicine_type);

        // Populate the spinner
        String[] medicineTypes = {"Tablets", "Capsules", "Syrups", "Injections", "Drops", "Ointments / Creams", "Inhalers", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                medicineTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedicineType.setAdapter(adapter);

        checkBeforeBreakfast = findViewById(R.id.check_before_breakfast);
        checkAfterBreakfast = findViewById(R.id.check_after_breakfast);
        checkBeforeLunch = findViewById(R.id.check_before_lunch);
        checkAfterLunch = findViewById(R.id.check_after_lunch);
        checkBeforeDinner = findViewById(R.id.check_before_dinner);
        checkAfterDinner = findViewById(R.id.check_after_dinner);

        checkEveryday = findViewById(R.id.check_everyday);
        checkMonday = findViewById(R.id.check_monday);
        checkTuesday = findViewById(R.id.check_tuesday);
        checkWednesday = findViewById(R.id.check_wednesday);
        checkThursday = findViewById(R.id.check_thursday);
        checkFriday = findViewById(R.id.check_friday);
        checkSaturday = findViewById(R.id.check_saturday);
        checkSunday = findViewById(R.id.check_sunday);

        btnSaveReminder = findViewById(R.id.btn_save_reminder);

        // Fetch meal times from Firestore (from the current user's document in the "User" collection)
        fetchMealTimes();

        btnSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveReminder();
            }
        });
    }

    private void fetchMealTimes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Example: "09:43 PM"
                        breakfastTimeStr = documentSnapshot.getString("breakfastTime");
                        lunchTimeStr = documentSnapshot.getString("lunchTime");
                        dinnerTimeStr = documentSnapshot.getString("dinnerTime");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNewMedReminderActivity.this,
                            "Failed to fetch meal times",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveReminder() {
        String medicineName = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String medicineType = spinnerMedicineType.getSelectedItem().toString();

        if (medicineName.isEmpty()) {
            Toast.makeText(this, "Please enter medicine name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dosage.isEmpty()) {
            Toast.makeText(this, "Please enter dosage", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if at least one intake time is selected
        if (!checkBeforeBreakfast.isChecked() && !checkAfterBreakfast.isChecked() &&
                !checkBeforeLunch.isChecked() && !checkAfterLunch.isChecked() &&
                !checkBeforeDinner.isChecked() && !checkAfterDinner.isChecked()) {
            Toast.makeText(this, "Please select at least one intake time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if at least one day is selected
        if (!checkEveryday.isChecked() && !checkMonday.isChecked() &&
                !checkTuesday.isChecked() && !checkWednesday.isChecked() &&
                !checkThursday.isChecked() && !checkFriday.isChecked() &&
                !checkSaturday.isChecked() && !checkSunday.isChecked()) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure meal times have been fetched
        if (breakfastTimeStr == null || lunchTimeStr == null || dinnerTimeStr == null) {
            Toast.makeText(this, "Meal times not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and populate the reminder model
        MedicineReminderModel reminder = new MedicineReminderModel();
        reminder.setId(String.valueOf(System.currentTimeMillis())); // unique ID
        reminder.setMedicineName(medicineName);
        reminder.setMedicineType(medicineType);
        reminder.setDosage(dosage);

        reminder.setBeforeBreakfast(checkBeforeBreakfast.isChecked());
        reminder.setAfterBreakfast(checkAfterBreakfast.isChecked());
        reminder.setBeforeLunch(checkBeforeLunch.isChecked());
        reminder.setAfterLunch(checkAfterLunch.isChecked());
        reminder.setBeforeDinner(checkBeforeDinner.isChecked());
        reminder.setAfterDinner(checkAfterDinner.isChecked());

        reminder.setEveryday(checkEveryday.isChecked());
        reminder.setMonday(checkMonday.isChecked());
        reminder.setTuesday(checkTuesday.isChecked());
        reminder.setWednesday(checkWednesday.isChecked());
        reminder.setThursday(checkThursday.isChecked());
        reminder.setFriday(checkFriday.isChecked());
        reminder.setSaturday(checkSaturday.isChecked());
        reminder.setSunday(checkSunday.isChecked());

        // Ensure meal times have been fetched
        if (breakfastTimeStr == null || lunchTimeStr == null || dinnerTimeStr == null) {
            Toast.makeText(this, "Meal times not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse the meal time strings (format "hh:mm a", e.g. "09:43 PM")
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            // BREAKFAST
            Date breakfastDate = sdf.parse(breakfastTimeStr);
            Calendar breakfastCal = Calendar.getInstance();
            breakfastCal.setTime(breakfastDate);
            int breakfastHour = breakfastCal.get(Calendar.HOUR_OF_DAY);
            int breakfastMinute = breakfastCal.get(Calendar.MINUTE);

            // LUNCH
            Date lunchDate = sdf.parse(lunchTimeStr);
            Calendar lunchCal = Calendar.getInstance();
            lunchCal.setTime(lunchDate);
            int lunchHour = lunchCal.get(Calendar.HOUR_OF_DAY);
            int lunchMinute = lunchCal.get(Calendar.MINUTE);

            // DINNER
            Date dinnerDate = sdf.parse(dinnerTimeStr);
            Calendar dinnerCal = Calendar.getInstance();
            dinnerCal.setTime(dinnerDate);
            int dinnerHour = dinnerCal.get(Calendar.HOUR_OF_DAY);
            int dinnerMinute = dinnerCal.get(Calendar.MINUTE);

            // Determine the days selected: If "Everyday" is checked, schedule for all days; otherwise only for selected days.
            boolean[] daysSelected = new boolean[7]; // index 0 = Sunday, 1 = Monday, …, 6 = Saturday
            if (checkEveryday.isChecked()) {
                for (int i = 0; i < 7; i++) {
                    daysSelected[i] = true;
                }
            } else {
                daysSelected[0] = checkSunday.isChecked();
                daysSelected[1] = checkMonday.isChecked();
                daysSelected[2] = checkTuesday.isChecked();
                daysSelected[3] = checkWednesday.isChecked();
                daysSelected[4] = checkThursday.isChecked();
                daysSelected[5] = checkFriday.isChecked();
                daysSelected[6] = checkSaturday.isChecked();
            }

            // Schedule alarms for each selected day and each checked intake option
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                if (!daysSelected[dayIndex]) continue;
                // In Calendar, Sunday=1, Monday=2, …, Saturday=7
                int calendarDay = (dayIndex == 0) ? Calendar.SUNDAY : dayIndex + 1;

                // Before Breakfast => -15 minutes
                if (checkBeforeBreakfast.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, breakfastHour, breakfastMinute, -15);
                    String alarmId = reminder.getId() + "_beforeBreakfast_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (Before Breakfast)",
                            alarmTime
                    );
                }

                // After Breakfast => +45 minutes
                if (checkAfterBreakfast.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, breakfastHour, breakfastMinute, 45);
                    String alarmId = reminder.getId() + "_afterBreakfast_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (After Breakfast)",
                            alarmTime
                    );
                }

                // Before Lunch => -15 minutes
                if (checkBeforeLunch.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, lunchHour, lunchMinute, -15);
                    String alarmId = reminder.getId() + "_beforeLunch_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (Before Lunch)",
                            alarmTime
                    );
                }

                // After Lunch => +45 minutes
                if (checkAfterLunch.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, lunchHour, lunchMinute, 45);
                    String alarmId = reminder.getId() + "_afterLunch_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (After Lunch)",
                            alarmTime
                    );
                }

                // Before Dinner => -15 minutes
                if (checkBeforeDinner.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, dinnerHour, dinnerMinute, -15);
                    String alarmId = reminder.getId() + "_beforeDinner_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (Before Dinner)",
                            alarmTime
                    );
                }

                // After Dinner => +45 minutes
                if (checkAfterDinner.isChecked()) {
                    long alarmTime = getNextOccurrence(calendarDay, dinnerHour, dinnerMinute, 45);
                    String alarmId = reminder.getId() + "_afterDinner_" + calendarDay;
                    MedicineAlarmUtils.setAlarm(
                            this,
                            alarmId,
                            "Please Take " + medicineName + " " + dosage + " " + medicineType + " (After Dinner)",
                            alarmTime
                    );
                }
            }

            // ======================================================
            // Store the reminder in Realtime Database under the user
            // ======================================================
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference remindersRef = FirebaseDatabase.getInstance()
                    .getReference("MedicineReminders")
                    .child(currentUserId); // Store under this user

            remindersRef.child(reminder.getId()).setValue(reminder)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddNewMedReminderActivity.this,
                                "Medicine reminder set successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseError", "Failed to save to Realtime Database", e);
                        Toast.makeText(AddNewMedReminderActivity.this,
                                "Failed to save to Realtime Database: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error parsing meal times or scheduling alarm",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns the next occurrence (in milliseconds) for a given day of the week at a specified hour/minute,
     * adjusted by offsetMinutes (positive or negative). If the computed time is in the past, it adds 7 days.
     */
    private long getNextOccurrence(int dayOfWeek, int hour, int minute, int offsetMinutes) {
        Calendar now = Calendar.getInstance();
        Calendar alarmTime = Calendar.getInstance();

        alarmTime.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);

        // Apply offset (e.g. -15 or +45)
        alarmTime.add(Calendar.MINUTE, offsetMinutes);

        // If the scheduled time is before 'now', add one week
        if (alarmTime.getTimeInMillis() <= now.getTimeInMillis()) {
            alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return alarmTime.getTimeInMillis();
    }
}
