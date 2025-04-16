package com.example.elderease.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elderease.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TaskReminderActivity extends AppCompatActivity {

    private EditText taskTitleEditText;
    private TextView selectedTimeTextView;
    private Button selectTimeButton, addTaskButton;
    private RecyclerView recyclerView;
    private TaskReminderAdapter adapter;
    private DatabaseReference databaseReference;
    private List<TaskReminderModel> remindersList;
    private long selectedTimeMillis = 0;
    private AlarmManager alarmManager;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_reminder);

        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        recyclerView = findViewById(R.id.tasksRecyclerView);

        selectedTimeTextView.setVisibility(View.GONE);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        remindersList = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Reminders").child(userId);

            adapter = new TaskReminderAdapter(this, remindersList, userId);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            finish();
        }

        selectTimeButton.setOnClickListener(v -> pickDateTime());

        addTaskButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Enable exact alarms in settings", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                    return;
                }
            }

            String taskTitle = taskTitleEditText.getText().toString().trim();

            if (taskTitle.isEmpty() || selectedTimeMillis == 0) {
                Toast.makeText(this, "Please enter task name and select time", Toast.LENGTH_SHORT).show();
                return;
            }

            String taskId = UUID.randomUUID().toString();
            TaskReminderModel task = new TaskReminderModel(taskId, taskTitle, selectedTimeMillis);
            databaseReference.child(taskId).setValue(task);

            AlarmUtils.setAlarm(this, taskId, taskTitle, selectedTimeMillis);

            Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show();
            taskTitleEditText.setText("");

            selectedTimeMillis = 0;
            selectedTimeTextView.setText("");
            selectedTimeTextView.setVisibility(View.GONE);

            fetchReminders();
        });

        fetchReminders();
    }

    private void fetchReminders() {
        if (user == null) return;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                remindersList.clear();
                long currentTimeMillis = System.currentTimeMillis();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TaskReminderModel reminder = dataSnapshot.getValue(TaskReminderModel.class);

                    if (reminder != null) {
                        if (reminder.getTimestamp() < currentTimeMillis) {
                            databaseReference.child(reminder.getId()).removeValue();
                        } else {
                            remindersList.add(reminder);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                TextView noRemindersTextView = findViewById(R.id.noRemindersTextView);
                if (remindersList.isEmpty()) {
                    noRemindersTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noRemindersTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskReminderActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void pickDateTime() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Calendar now = Calendar.getInstance();

            // Ensure that the selected date is not in the past
            if (calendar.getTimeInMillis() < now.getTimeInMillis() - 60000) {
                Toast.makeText(this, "Cannot select past date", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            selectedTimeTextView.setText("Date: " + dateFormat.format(calendar.getTime()));

            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                // Check if the selected time is in the past when choosing today
                if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                        calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {

                    if (calendar.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY) ||
                            (calendar.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY) &&
                                    calendar.get(Calendar.MINUTE) < now.get(Calendar.MINUTE))) {
                        Toast.makeText(this, "Cannot select past time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                selectedTimeMillis = calendar.getTimeInMillis();

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                selectedTimeTextView.setText(dateTimeFormat.format(calendar.getTime()));
                selectedTimeTextView.setVisibility(View.VISIBLE);

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }
}
