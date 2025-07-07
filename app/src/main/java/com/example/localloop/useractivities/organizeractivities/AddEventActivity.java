package com.example.localloop.useractivities.organizeractivities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.Event;
import com.example.localloop.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddEventActivity extends AppCompatActivity {



    private EditText editTextTitle, editTextDescription, editTextLocation, editTextFee;
    private Spinner spinnerCategory;
    private Button buttonAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event); // Ensure your XML file is correctly named

        // Reference UI fields
        editTextTitle = findViewById(R.id.title);
        editTextDescription = findViewById(R.id.description);
        editTextLocation = findViewById(R.id.location);
        editTextFee = findViewById(R.id.fee);
        spinnerCategory = findViewById(R.id.category);
        buttonAddEvent = findViewById(R.id.buttonAddEvent);


        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Wedding");
        categoryList.add("Music");
        categoryList.add("New Year");
        categoryList.add("Award Ceremony");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        buttonAddEvent.setOnClickListener(v -> addEventToFirebase());
    }

    private void addEventToFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        double fee = Double.parseDouble(editTextFee.getText().toString().trim());
        String category = spinnerCategory.getSelectedItem().toString();
        String dateAndTime = "2025-07-10 18:00";
        int maxParticipants = 50;

        // Validation
        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double fees;
        if (fee < 0){
            fees = 0.0;
        } else {
            try {
                fees = 1;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Fee must be a numeric value.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create Event object
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        String eventId = databaseReference.push().getKey();


        Event event = new Event(title, description, category, dateAndTime, location, maxParticipants, fee, eventId);


        // Push to Firebase
        databaseReference.child(eventId).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    finish(); // Return to dashboard
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add event. Try again.", Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextLocation.setText("");
        editTextFee.setText("");
        spinnerCategory.setSelection(0);
    }
    }




