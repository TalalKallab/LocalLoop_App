package com.example.localloop.useractivities.adminactivities.manageuseraccounts;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;

import com.example.localloop.R;
import com.example.localloop.entities.User;
import com.example.localloop.helpers.Firebase;

import java.util.List;
import java.util.Map;

public class ManageUserAccountsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_accounts);

        LinearLayout userListContainer = findViewById(R.id.userListContainer);

        Firebase.fetchAllUsers(new Firebase.UserListCallback() {
            @Override
            public void onUserListFetched(List<Map<String, String>> users) {
                userListContainer.removeAllViews();

                for (Map<String, String> user : users) {
                    String name = user.get("Name");
                    String email = user.get("Email");
                    String role = user.get("Role");

                    TextView textViewUserInfo = new TextView(ManageUserAccountsActivity.this);
                    textViewUserInfo.setText("Name: " + name + "\nEmail: " + email + "\nRole: " + role);
                    textViewUserInfo.setTextSize(16);
                    textViewUserInfo.setPadding(24, 24, 24, 24);
                    textViewUserInfo.setTextColor(Color.BLACK);
                    textViewUserInfo.setBackgroundColor(Color.parseColor("#EEEEEE"));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 32);
                    textViewUserInfo.setLayoutParams(params);

                    textViewUserInfo.setOnClickListener(v -> {
                        new AlertDialog.Builder(ManageUserAccountsActivity.this)
                                .setTitle("Manage User")
                                .setMessage("What would you like to do with \"" + name + "\"?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    Firebase.deleteUser(email, new Firebase.UserFetchCallback() {
                                        @Override
                                        public void onSuccess(User user) {
                                            Toast.makeText(ManageUserAccountsActivity.this, "User deleted.", Toast.LENGTH_SHORT).show();
                                            userListContainer.removeView(textViewUserInfo);
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            Toast.makeText(ManageUserAccountsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .setNegativeButton("Disable", (dialog, which) -> {
                                    Firebase.disableUser(email, new Firebase.UserFetchCallback() {
                                        @Override
                                        public void onSuccess(User user) {
                                            Toast.makeText(ManageUserAccountsActivity.this, "User disabled.", Toast.LENGTH_SHORT).show();
                                            // Optional: Update UI (e.g., grey out or add "[DISABLED]" tag)
                                            textViewUserInfo.setBackgroundColor(Color.LTGRAY);
                                            textViewUserInfo.setText(textViewUserInfo.getText() + "\nStatus: Disabled");
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            Toast.makeText(ManageUserAccountsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                                .setNeutralButton("Cancel", null)
                                .show();
                    });

                    userListContainer.addView(textViewUserInfo);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ManageUserAccountsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
