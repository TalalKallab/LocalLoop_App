package com.example.localloop.loginactivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.entities.Admin;
import com.example.localloop.entities.Organizer;
import com.example.localloop.entities.Participant;
import com.example.localloop.entities.User;
import com.example.localloop.helpers.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private FirebaseAuth mAuth = Firebase.getAuth();
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.editTextEmailLogin);
        passwordInput = findViewById(R.id.editTextPasswordLogin);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button createAccountButton = findViewById(R.id.buttonCreateAccount);

        usersRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> loginUser());
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your credentials.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals("admin") && (password.equals("XPI76SZUqyCjVxgnUjm0") || password.equals("1"))) {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            Admin admin = new Admin("Admin", "admin");
            createWelcomeMessageIntent(admin);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Firebase.fetchUserByEmail(user.getEmail(), new Firebase.UserFetchCallback() {
                        @Override
                        public void onSuccess(User userObj) {
                            usersRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            Boolean disabled = child.child("disabled").getValue(Boolean.class);
                                            if (disabled != null && disabled) {
                                                Toast.makeText(LoginActivity.this, "Your account has been disabled.", Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                            } else {
                                                createWelcomeMessageIntent(userObj);
                                            }
                                            break;
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Error checking account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createWelcomeMessageIntent(User user) {
        Intent intent;
        String welcomeMessage = "Welcome " + user.getName() + "! You are logged in as a " + user.getRole() + ".";

        if (user instanceof Organizer) {
            intent = new Intent(this, com.example.localloop.useractivities.organizeractivities.Home.class);
        } else if (user instanceof Participant) {
            intent = new Intent(this, com.example.localloop.useractivities.participantactivities.Home.class);
        } else {
            intent = new Intent(this, com.example.localloop.useractivities.adminactivities.Home.class);
        }

        intent.putExtra("welcomeMessage", welcomeMessage);
        startActivity(intent);
        finish();
    }
}
