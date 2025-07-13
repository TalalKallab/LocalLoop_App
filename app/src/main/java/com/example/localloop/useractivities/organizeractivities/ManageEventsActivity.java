// ManageEventsActivity.java using Firebase Realtime Database
package com.example.localloop.useractivities.organizeractivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.Event;
import com.example.localloop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageEventsActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> eventTitles;
    private HashMap<String, Event> eventMap;
    private ArrayAdapter<String> adapter;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        listView = findViewById(R.id.eventsListView);
        eventTitles = new ArrayList<>();
        eventMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventTitles);
        listView.setAdapter(adapter);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        loadEvents();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String title = eventTitles.get(position);
            Event selectedEvent = eventMap.get(title);
            if (selectedEvent != null) {
                Intent intent = new Intent(ManageEventsActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", selectedEvent.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: missing Event ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEvents() {
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventTitles.clear();
                eventMap.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventTitles.add(event.getTitle());
                        eventMap.put(event.getTitle(), event);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
