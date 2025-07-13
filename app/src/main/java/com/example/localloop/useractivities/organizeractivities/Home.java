package com.example.localloop.useractivities.organizeractivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        // Set welcome message
        String welcomeMessage = getIntent().getStringExtra("welcomeMessage");
        TextView welcomeText = findViewById(R.id.textViewDashboardMessage);
        if (welcomeMessage != null && !welcomeMessage.isEmpty()) {
            welcomeText.setText(welcomeMessage);
        } else {
            welcomeText.setText("Welcome Organizer!");
        }

        // Initialize buttons
        Button addEventButton = findViewById(R.id.buttonAddEvent);
        Button manageEventsButton = findViewById(R.id.buttonManageEvents);

        // Set click listeners
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddEventActivity.class);
            startActivity(intent);
        });

        manageEventsButton.setOnClickListener(v -> {
            // Temporarily route to EditEventActivity since ManageEventsActivity doesn't exist
            Intent intent = new Intent(Home.this, EditEventActivity.class);
            startActivity(intent);
        });
    }
}
