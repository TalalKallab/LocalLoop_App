package com.example.localloop.loginactivities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nameInput, emailInput, passwordInput;
    private Spinner roleInput;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        nameInput = findViewById(R.id.editTextName);
        roleInput = findViewById(R.id.spinnerRole);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        Button registerButton = findViewById(R.id.buttonRegister);

        // Spinner dropdown setup
        String[] roles = {"Choose Role", "Organizer", "Participant"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleInput.setAdapter(adapter);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String role = roleInput.getSelectedItem().toString();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty() || role.equals("Choose Role") || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter valid credentials.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                // Save user info to Realtime Database
                Map<String, Object> user = new HashMap<>();
                user.put("Name", name);
                user.put("Role", role);
                user.put("Email", email);
                user.put("disabled", false);

                dbRef.child("users").push().setValue(user)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "User saved to database!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
