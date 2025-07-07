package com.example.localloop.useractivities.organizeractivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditEventActivity extends AppCompatActivity {

    // Declare EditTexts, Spinner, Button variables
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextLocation;
    private EditText editTextFee;
    private EditText editTextDateAndTime;
    private Spinner spinnerCategory;
    private Button buttonUpdateEvent;
    private Button buttonDeleteEvent;

    private DatabaseReference eventsRef;
    private DatabaseReference categoriesRef;
    private String eventId;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize views
        editTextTitle = findViewById(R.id.title);
        editTextDescription = findViewById(R.id.description);
        editTextLocation = findViewById(R.id.location);
        editTextFee = findViewById(R.id.fee);
        editTextDateAndTime = findViewById(R.id.dateAndTime);
        spinnerCategory = findViewById(R.id.category);
        buttonUpdateEvent = findViewById(R.id.updateEvent);
        buttonDeleteEvent = findViewById(R.id.deleteEvent);

        // Initialize Firebase references
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        // Get eventId from Intent
        eventId = getIntent().getStringExtra("eventId");

        // Populate category spinner
        loadCategoriesIntoSpinner();

        // Fetch event details and pre-fill fields
        fetchEventDetails();

        // Update Event button listener
        buttonUpdateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    updateEventInFirebase();
                }
            }
        });

        // Delete Event button listener
        buttonDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAndDeleteEvent();
            }
        });
    }

    private void loadCategoriesIntoSpinner() {
        ArrayList<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String category = dataSnapshot.getValue(String.class);
                    categories.add(category);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEventActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEventDetails() {
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentEvent = snapshot.getValue(Event.class);
                if (currentEvent != null) {
                    editTextTitle.setText(currentEvent.getTitle());
                    editTextDescription.setText(currentEvent.getDescription());
                    editTextLocation.setText(currentEvent.getLocation());
                    editTextFee.setText(String.valueOf(currentEvent.getFee()));
                    editTextDateAndTime.setText(currentEvent.getDateAndTime());

                    // Set category in spinner
                    String eventCategory = currentEvent.getCategory();
                    ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                    int position = adapter.getPosition(eventCategory);
                    spinnerCategory.setSelection(position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditEventActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String feeString = editTextFee.getText().toString().trim();
        String dateAndTime = editTextDateAndTime.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(location) || TextUtils.isEmpty(dateAndTime)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Double.parseDouble(feeString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a numeric value", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateEventInFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        double fee = Double.parseDouble(editTextFee.getText().toString().trim());
        String dateAndTime = editTextDateAndTime.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        Event updatedEvent = new Event(title, description, category, dateAndTime, location, currentEvent.getMAX_PARTICIPANTS(), fee, eventId);

        eventsRef.child(eventId).setValue(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show());
    }

    private void confirmAndDeleteEvent() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteEvent() {
        eventsRef.child(eventId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }
}
