package com.example.elderease.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.elderease.R;
import com.example.elderease.chatbot.ChatbotActivity;
import com.example.elderease.dailytips.DailyTipsActivity;
import com.example.elderease.exercises.ExerciseCategoryActivity;
import com.example.elderease.fbgroups.ElderlyGroupsActivity;
import com.example.elderease.games.GamesActivity;
import com.example.elderease.medicinesreminder.MedicinesReminderActivity;
import com.example.elderease.news.NewsActivity;
import com.example.elderease.sos.DisplayEmergencyContactsActivity;
import com.example.elderease.sos.SosConfirmationActivity;
import com.example.elderease.tasks.TaskReminderActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FusedLocationProviderClient fusedLocationClient;
    Button playGamesButton, medicineRemindersButton, taskRemindersButton, exerciseButton, grpButton, chatbotButton, sosButton, emerContactBtn, trendingNewsBtn, dailyTipsBtn;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar myToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.logout);
        myToolbar.setNavigationOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        });

        playGamesButton = findViewById(R.id.games);
        taskRemindersButton = findViewById(R.id.taskReminders);
        medicineRemindersButton = findViewById(R.id.medicineReminders);
        exerciseButton = findViewById(R.id.exercise);
        grpButton = findViewById(R.id.elderGrp);
        chatbotButton = findViewById(R.id.chatbot);
        sosButton = findViewById(R.id.sos);
        emerContactBtn = findViewById(R.id.emergencyContacts);
        trendingNewsBtn = findViewById(R.id.trendingNews);
        dailyTipsBtn = findViewById(R.id.dailyTips);

        // Request permissions when the app opens
        requestPermissionsIfNeeded();

        // Check if location services are enabled
        checkLocationEnabled();

        // SOS Button Click Listener
        sosButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                sendSOS();
            } else {
                requestPermissionsIfNeeded();
            }
        });

        // Other button listeners remain unchanged
        playGamesButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GamesActivity.class)));
        exerciseButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExerciseCategoryActivity.class)));
        grpButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ElderlyGroupsActivity.class)));
        chatbotButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatbotActivity.class)));
        emerContactBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DisplayEmergencyContactsActivity.class)));
        taskRemindersButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TaskReminderActivity.class)));
        medicineRemindersButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MedicinesReminderActivity.class)));
        trendingNewsBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewsActivity.class)));
        dailyTipsBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DailyTipsActivity.class)));
    }

    private void requestPermissionsIfNeeded() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.POST_NOTIFICATIONS
            }, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "Permissions are required for the app to function properly.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void sendSOS() {
        try {
            getUserLocation();
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String locationMessage = "My current location: https://maps.google.com/?q=" + latitude + "," + longitude;
                        getEmergencyContacts(locationMessage);
                    } else {
                        Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show());
    }

    private void getEmergencyContacts(String locationMessage) {
        firestore.collection("User").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> emergencyContacts = (List<Map<String, Object>>) documentSnapshot.get("emergency_contacts");
                        if (emergencyContacts != null) {
                            sendSOSMessageFromList(emergencyContacts, locationMessage);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to fetch contacts", Toast.LENGTH_SHORT).show());
    }

    private void sendSOSMessageFromList(List<Map<String, Object>> emergencyContacts, String locationMessage) {
        String emergencyMessage = "Emergency! I need help. " + locationMessage;

        for (Map<String, Object> contact : emergencyContacts) {
            String number = (String) contact.get("number");
            if (number != null) {
                sendSMS(number, emergencyMessage);
            }
        }

        startActivity(new Intent(MainActivity.this, SosConfirmationActivity.class));
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
    }
}
