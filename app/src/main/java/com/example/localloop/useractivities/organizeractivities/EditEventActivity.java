package com.example.localloop.useractivities.organizeractivities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.Event;
import com.example.localloop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditEventActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, locationInput, feeInput, dateInput;
    private Spinner categorySpinner;
    private Button updateBtn, deleteBtn;

    private DatabaseReference eventsRef;
    private String eventId;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        titleInput = findViewById(R.id.title);
        descriptionInput = findViewById(R.id.description);
        locationInput = findViewById(R.id.location);
        feeInput = findViewById(R.id.fee);
        dateInput = findViewById(R.id.dateAndTime);
        categorySpinner = findViewById(R.id.category);
        updateBtn = findViewById(R.id.updateEvent);
        deleteBtn = findViewById(R.id.deleteEvent);

        // Example categories
        String[] categories = {"Wedding", "Conference", "Seminar", "Birthday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Missing Event ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        fetchEvent();

        updateBtn.setOnClickListener(v -> {
            if (validateFields()) {
                updateEvent();
            }
        });

        deleteBtn.setOnClickListener(v -> deleteEvent());
    }

    private void fetchEvent() {
        eventsRef.child(eventId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                currentEvent = snapshot.getValue(Event.class);
                if (currentEvent != null) {
                    titleInput.setText(currentEvent.getTitle());
                    descriptionInput.setText(currentEvent.getDescription());
                    locationInput.setText(currentEvent.getLocation());
                    feeInput.setText(String.valueOf(currentEvent.getFee()));
                    dateInput.setText(currentEvent.getDateAndTime());

                    String cat = currentEvent.getCategory();
                    ArrayAdapter adapter = (ArrayAdapter) categorySpinner.getAdapter();
                    int pos = adapter.getPosition(cat);
                    categorySpinner.setSelection(pos >= 0 ? pos : 0);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private boolean validateFields() {
        return !TextUtils.isEmpty(titleInput.getText()) &&
                !TextUtils.isEmpty(descriptionInput.getText()) &&
                !TextUtils.isEmpty(locationInput.getText()) &&
                !TextUtils.isEmpty(feeInput.getText()) &&
                !TextUtils.isEmpty(dateInput.getText());
    }

    private void updateEvent() {
        String title = titleInput.getText().toString().trim();
        String desc = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        double fee = Double.parseDouble(feeInput.getText().toString().trim());
        String date = dateInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        Event updated = new Event(title, desc, category, date, location, currentEvent.getMAX_PARTICIPANTS(), fee, eventId);
        eventsRef.child(eventId).setValue(updated)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent() {
        eventsRef.child(eventId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
