package com.example.elderease.sos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.example.elderease.R;

public class SosConfirmationActivity extends AppCompatActivity {

    private TextView sosStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_confirmation);
    }
}
