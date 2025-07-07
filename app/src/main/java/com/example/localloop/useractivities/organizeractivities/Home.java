package com.example.localloop.useractivities.organizeractivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;

import android.widget.TextView;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        String welcomeMessage = getIntent().getStringExtra("welcomeMessage");
        TextView welcomeText = findViewById(R.id.textViewDashboardMessage);
        welcomeText.setText(welcomeMessage);

    }
}
