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
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

            // Category category = new Category(name, description);
            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("Name", name);
            categoryMap.put("Description", description);

            FirebaseFirestore.getInstance()
                    .collection("categories").add(categoryMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ManageCategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    // This is so the previous outdated page isn't shown
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
