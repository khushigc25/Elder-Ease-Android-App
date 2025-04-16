package com.example.elderease.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.elderease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, mobno, password, dob;
    RadioGroup genderGroup;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser()!=null) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobno = findViewById(R.id.mobno);
        password = findViewById(R.id.password);
        dob = findViewById(R.id.dob);
        genderGroup = findViewById(R.id.radioGroupGender);  // RadioGroup for gender selection

        // Date Picker Dialog for Date of Birth
        dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Setting the selected date in the EditText
                        dob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    public void signUp(View view) {
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userMobNo = mobno.getText().toString();
        String userPassword = password.getText().toString();
        String userDob = dob.getText().toString();

        // Get selected gender
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String userGender = selectedGender != null ? selectedGender.getText().toString() : "";

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userMobNo)) {
            Toast.makeText(this, "Enter your Mobile Number!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password is too short! Please enter at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userMobNo.length() < 10) {
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userGender)) {
            Toast.makeText(this, "Please select a gender!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userDob)) {
            Toast.makeText(this, "Please select your date of birth!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", userName);
                            userData.put("email", userEmail);
                            userData.put("phone", userMobNo);
                            userData.put("dob", userDob);
                            userData.put("gender", userGender);

                            firestore.collection("User").document(userId)
                                    .set(userData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            // Redirect to AddEmergencyContactsActivity
                                            Intent intent = new Intent(RegistrationActivity.this, AddEmergencyContactsActivity.class);
                                            intent.putExtra("userId", userId);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Failed to create account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)); // Redirect to LoginActivity for sign-in
    }
}
