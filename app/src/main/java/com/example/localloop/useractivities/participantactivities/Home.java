package com.example.localloop.useractivities.participantactivities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_dashboard);

        String welcomeMessage = getIntent().getStringExtra("welcomeMessage");
        TextView welcomeText = findViewById(R.id.textViewDashboardMessage);
        welcomeText.setText(welcomeMessage);
    }
}
