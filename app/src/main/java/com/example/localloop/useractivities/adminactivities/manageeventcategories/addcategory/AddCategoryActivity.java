package com.example.localloop.useractivities.adminactivities.manageeventcategories.addcategory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.useractivities.adminactivities.manageeventcategories.ManageCategoriesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextCategoryName;
    private EditText editTextCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("categories");

        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = findViewById(R.id.editTextCategoryDescription);
        Button buttonSubmitCategory = findViewById(R.id.buttonSubmitCategory);

        buttonSubmitCategory.setOnClickListener(v -> {
            String name = editTextCategoryName.getText().toString().trim();
            String description = editTextCategoryDescription.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("Name", name);
            categoryMap.put("Description", description);

            dbRef.push().setValue(categoryMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ManageCategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
