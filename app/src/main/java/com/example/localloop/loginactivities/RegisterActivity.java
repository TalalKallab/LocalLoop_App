package com.example.localloop.loginactivities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.example.localloop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.HashMap;

import android.widget.Spinner;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nameInput, emailInput, passwordInput;
    private Spinner roleInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        nameInput = findViewById(R.id.editTextName);
        roleInput = findViewById(R.id.spinnerRole);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        Button registerButton = findViewById(R.id.buttonRegister);

        // Spinner dropdown menu functionality
        String[] roles = {"Choose Role", "Organizer", "Participant"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleInput.setAdapter(adapter);

        registerButton.setOnClickListener(v -> registerUser(db));
    }

    private void registerUser(FirebaseFirestore db) {
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

                // *** if user is signed up succesfully, store their info in firestore here ***
                Map<String, Object> user = new HashMap<>();
                user.put("Name", name);
                user.put("Role", role);
                user.put("Email", email);

                db.collection("users").add(user)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error adding document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}