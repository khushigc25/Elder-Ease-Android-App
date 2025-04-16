package com.example.elderease.medicinesreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditMealTimeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentReference userRef;

    private TextView breakfastTimeText, lunchTimeText, dinnerTimeText;
    private Button editBreakfastBtn, editLunchBtn, editDinnerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal_time);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI components
        breakfastTimeText = findViewById(R.id.breakfast_time_text);
        lunchTimeText = findViewById(R.id.lunch_time_text);
        dinnerTimeText = findViewById(R.id.dinner_time_text);

        editBreakfastBtn = findViewById(R.id.edit_breakfast_btn);
        editLunchBtn = findViewById(R.id.edit_lunch_btn);
        editDinnerBtn = findViewById(R.id.edit_dinner_btn);

        if (user != null) {
            userRef = db.collection("User").document(user.getUid());
            loadMealTimes(); // Fetch and display meal times
        }

        // Set listeners for edit buttons
        editBreakfastBtn.setOnClickListener(v -> showTimePickerDialog("breakfastTime", breakfastTimeText));
        editLunchBtn.setOnClickListener(v -> showTimePickerDialog("lunchTime", lunchTimeText));
        editDinnerBtn.setOnClickListener(v -> showTimePickerDialog("dinnerTime", dinnerTimeText));
    }

    private void loadMealTimes() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    breakfastTimeText.setText(document.getString("breakfastTime"));
                    lunchTimeText.setText(document.getString("lunchTime"));
                    dinnerTimeText.setText(document.getString("dinnerTime"));
                }
            } else {
                Toast.makeText(this, "Failed to load meal times", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimePickerDialog(String mealType, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String amPm = (selectedHour >= 12) ? "PM" : "AM";
                    int hour12 = (selectedHour == 0 || selectedHour == 12) ? 12 : selectedHour % 12;
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, selectedMinute, amPm);

                    timeTextView.setText(formattedTime);
                    updateMealTime(mealType, formattedTime);
                }, hour, minute, false);

        timePickerDialog.show();
    }

    private void updateMealTime(String mealType, String newTime) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(mealType, newTime);

        userRef.set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Meal time updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update meal time", Toast.LENGTH_SHORT).show());
    }
}
