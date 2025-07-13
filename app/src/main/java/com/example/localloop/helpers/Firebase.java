package com.example.localloop.helpers;

import com.example.localloop.entities.Admin;
import com.example.localloop.entities.Organizer;
import com.example.localloop.entities.Participant;
import com.example.localloop.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firebase {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public interface UserFetchCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static DatabaseReference getDb() {
        return db;
    }

    public static void fetchUserByEmail(String email, UserFetchCallback callback) {
        db.child("users").orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String name = child.child("Name").getValue(String.class);
                        String role = child.child("Role").getValue(String.class);

                        User user;
                        switch (role) {
                            case "Admin":
                                user = new Admin(name, "admin");
                                break;
                            case "Organizer":
                                user = new Organizer(name);
                                break;
                            default:
                                user = new Participant(name);
                                break;
                        }
                        callback.onSuccess(user);
                        return;
                    }
                } else {
                    callback.onError("No user found with this email.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error fetching user: " + error.getMessage());
            }
        });
    }

    public static void fetchAllUsers(UserListCallback callback) {
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Map<String, String>> userList = new ArrayList<>();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("Name", doc.child("Name").getValue(String.class));
                    userMap.put("Email", doc.child("Email").getValue(String.class));
                    userMap.put("Role", doc.child("Role").getValue(String.class));
                    userList.add(userMap);
                }
                callback.onUserListFetched(userList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Failed to load users: " + error.getMessage());
            }
        });
    }

    public static void deleteUser(String email, UserFetchCallback callback) {
        db.child("users").orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                                .addOnFailureListener(e -> callback.onError("Error deleting user: " + e.getMessage()));
                        return;
                    }
                } else {
                    callback.onError("No user found with email: " + email);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error finding user: " + error.getMessage());
            }
        });
    }

    public static void disableUser(String email, UserFetchCallback callback) {
        db.child("users").orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        child.getRef().child("disabled").setValue(true)
                                .addOnSuccessListener(aVoid -> {
                                    String name = child.child("Name").getValue(String.class);
                                    String role = child.child("Role").getValue(String.class);
                                    User user;
                                    switch (role) {
                                        case "Admin":
                                            user = new Admin(name, "admin");
                                            break;
                                        case "Organizer":
                                            user = new Organizer(name);
                                            break;
                                        default:
                                            user = new Participant(name);
                                            break;
                                    }
                                    callback.onSuccess(user);
                                })
                                .addOnFailureListener(e -> callback.onError("Failed to disable user: " + e.getMessage()));
                        return;
                    }
                } else {
                    callback.onError("No user found with email: " + email);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error finding user: " + error.getMessage());
            }
        });
    }

    // === CATEGORY METHODS ===
    public static void fetchAllCategories(CategoryListCallback callback) {
        db.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Map<String, String>> categoryList = new ArrayList<>();
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Map<String, String> categoryMap = new HashMap<>();
                    categoryMap.put("Name", doc.child("Name").getValue(String.class));
                    categoryMap.put("Description", doc.child("Description").getValue(String.class));
                    categoryList.add(categoryMap);
                }
                callback.onCategoryListFetched(categoryList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Failed to load categories: " + error.getMessage());
            }
        });
    }

    public static void deleteCategory(String name, FirebaseCallback callback) {
        db.child("categories").orderByChild("Name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot doc : snapshot.getChildren()) {
                        doc.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError("Error deleting category: " + e.getMessage()));
                    }
                } else {
                    callback.onError("No category found with name: " + name);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error searching for category: " + error.getMessage());
            }
        });
    }

    public static void editCategory(String oldName, String newName, String newDescription, FirebaseCallback callback) {
        db.child("categories").orderByChild("Name").equalTo(oldName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot doc : snapshot.getChildren()) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("Name", newName);
                        updates.put("Description", newDescription);

                        doc.getRef().updateChildren(updates)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError("Update failed: " + e.getMessage()));
                    }
                } else {
                    callback.onError("Category not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error finding category: " + error.getMessage());
            }
        });
    }

    // === INTERFACES ===
    public interface CategoryListCallback {
        void onCategoryListFetched(List<Map<String, String>> categories);
        void onError(String error);
    }

    public interface UserListCallback {
        void onUserListFetched(List<Map<String, String>> users);
        void onError(String error);
    }

    public interface FirebaseCallback {
        void onSuccess();
        void onError(String error);
    }
}
