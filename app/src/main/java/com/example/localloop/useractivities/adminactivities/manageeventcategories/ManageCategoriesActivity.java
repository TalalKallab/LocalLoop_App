package com.example.localloop.useractivities.adminactivities.manageeventcategories;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import com.example.localloop.R;
import com.example.localloop.helpers.*;
import com.example.localloop.useractivities.adminactivities.manageeventcategories.addcategory.AddCategoryActivity;

public class ManageCategoriesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        LinearLayout categoryListLayout = findViewById(R.id.categoryListLayout);

        Button addCategoryButton = findViewById(R.id.buttonAddCategory);
        //Button editCategoryButton = findViewById(R.id.buttonEditCategory);
        //Button deleteCategoryButton = findViewById(R.id.buttonDeleteCategory);

        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivity(intent);
        });

        /*editCategoryButton.setOnClickListener(v -> {

        });

        deleteCategoryButton.setOnClickListener(v -> {

        });*/

        // Fetches the categories from the "categories" collection in the firestore database
        Firebase.fetchAllCategories(new Firebase.CategoryListCallback() {
            @Override
            public void onCategoryListFetched(List<Map<String, String>> categories) {
                categoryListLayout.removeAllViews();

                for (Map<String, String> category : categories) {
                    String name = category.get("Name");
                    String description = category.get("Description");

                    TextView categoryView = new TextView(ManageCategoriesActivity.this);
                    categoryView.setText("Name: " + name + "\nDescription: " + description);
                    categoryView.setTextSize(16);
                    categoryView.setPadding(24, 24, 24, 24);
                    categoryView.setTextColor(android.graphics.Color.BLACK); // Black text
                    categoryView.setBackgroundColor(android.graphics.Color.parseColor("#EEEEEE"));

                    LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    categoryParams.setMargins(0, 0, 0, 32);
                    categoryView.setLayoutParams(categoryParams);

                    // Upon clicking
                    categoryView.setOnClickListener(v -> {
                        new android.app.AlertDialog.Builder(ManageCategoriesActivity.this).setTitle("Manage Category")
                                .setMessage("What do you want to do with \"" + name + "\"?")
                                .setPositiveButton("Edit", (dialog, which) -> {
                                    // The alert has 2 editText's for the name category name and description
                                    LinearLayout layout = new LinearLayout(ManageCategoriesActivity.this);
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setPadding(48, 24, 48, 0);

                                    EditText inputName = new EditText(ManageCategoriesActivity.this);
                                    inputName.setHint("New Category Name");
                                    layout.addView(inputName);

                                    EditText inputDescription = new EditText(ManageCategoriesActivity.this);
                                    inputDescription.setHint("New Category Description");
                                    layout.addView(inputDescription);

                                    new AlertDialog.Builder(ManageCategoriesActivity.this).setTitle("Edit Category")
                                            .setView(layout)
                                            .setPositiveButton("Submit", (editDialog, whichEdit) -> {
                                                String newName = inputName.getText().toString().trim();
                                                String newDescription = inputDescription.getText().toString().trim();

                                                if (newName.isEmpty() || newDescription.isEmpty()) {
                                                    Toast.makeText(ManageCategoriesActivity.this, "Both fields are required.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                // editCategory in the Firebase helper class is responsible for editing the category fields
                                                Firebase.editCategory(name, newName, newDescription, new Firebase.FirebaseCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Toast.makeText(ManageCategoriesActivity.this, "Category updated.", Toast.LENGTH_SHORT).show();
                                                        recreate();
                                                    }

                                                    @Override
                                                    public void onError(String error) {
                                                        Toast.makeText(ManageCategoriesActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }).setNegativeButton("Delete", (dialog, which) -> {
                                    categoryView.post(() -> {
                                        new android.app.AlertDialog.Builder(ManageCategoriesActivity.this)
                                                .setTitle("Confirm Delete")
                                                .setMessage("Are you sure you want to delete \"" + name + "\" from event categories?")
                                                .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                                    // deleteCategory in the Firebase helper class is responsible for deleting a specific category
                                                    Firebase.deleteCategory(name, new Firebase.FirebaseCallback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Toast.makeText(ManageCategoriesActivity.this, "Category deleted.", Toast.LENGTH_SHORT).show();
                                                            categoryListLayout.removeView(categoryView);
                                                        }

                                                        @Override
                                                        public void onError(String error) {
                                                            Toast.makeText(ManageCategoriesActivity.this, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                    });
                                }).setNeutralButton("Cancel", null).show();
                    });
                    categoryListLayout.addView(categoryView);
                }

            }

            @Override
            public void onError(String error) {
                Toast.makeText(ManageCategoriesActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
