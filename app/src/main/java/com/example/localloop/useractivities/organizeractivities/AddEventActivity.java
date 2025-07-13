package com.example.localloop.useractivities.organizeractivities;

import android.os.Bundle;
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
        setContentView(R.layout.activity_add_event);

        // Reference UI fields
        editTextTitle = findViewById(R.id.title);
        editTextDescription = findViewById(R.id.description);
        editTextLocation = findViewById(R.id.location);
        editTextFee = findViewById(R.id.fee);
        spinnerCategory = findViewById(R.id.category);
        buttonAddEvent = findViewById(R.id.buttonAddEvent);

        // Set up category spinner
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Wedding");
        categoryList.add("Music");
        categoryList.add("New Year");
        categoryList.add("Award Ceremony");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Handle Add Event button click
        buttonAddEvent.setOnClickListener(v -> addEventToFirebase());
    }

    private void addEventToFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String feeText = editTextFee.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String dateAndTime = "2025-07-10 18:00"; // Placeholder. Add DatePicker/TimePicker if needed.
        int maxParticipants = 50;

        // Basic validation
        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || feeText.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double fee;
        try {
            fee = Double.parseDouble(feeText);
            if (fee < 0) fee = 0.0;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        String eventId = databaseReference.push().getKey();

        // Create event and save it
        Event event = new Event(title, description, category, dateAndTime, location, maxParticipants, fee, eventId);
        databaseReference.child(eventId).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    finish(); // Go back to dashboard
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
