package com.example.localloop.useractivities.adminactivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;
import com.example.localloop.useractivities.adminactivities.manageeventcategories.ManageCategoriesActivity;
import com.example.localloop.useractivities.adminactivities.manageuseraccounts.ManageUserAccountsActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        String welcomeMessage = getIntent().getStringExtra("welcomeMessage");
        TextView welcomeText = findViewById(R.id.textViewDashboardMessage);
        welcomeText.setText(welcomeMessage);

        // "Manage Event Categories" functionality
        Button manageCategoriesButton = findViewById(R.id.buttonManageCategories);
        manageCategoriesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategoriesActivity.class);
            startActivity(intent);
        });

        // "Manage User Accounts" functionality
        Button buttonManageUserAccounts = findViewById(R.id.buttonManageUserAccounts);
        buttonManageUserAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUserAccountsActivity.class);
            startActivity(intent);
        });
    }
}
