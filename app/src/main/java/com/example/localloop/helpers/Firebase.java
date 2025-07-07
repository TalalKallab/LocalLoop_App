package com.example.localloop.helpers;

import com.example.localloop.entities.Admin;
import com.example.localloop.entities.Organizer;
import com.example.localloop.entities.Participant;
import com.example.localloop.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Firebase is needed when logging in to create the welcome message (fetches name + role from database),
   it is also needed for example when admin wants to view a list of all users (all user info needs to be fetched)
   The purpose of this class is to modularize it, an instance of it can be invoked as opposed to having to recopy the entire firebase code functionality */
public class Firebase {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface UserFetchCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    // Fetches the user's information (name and role) using their email as the access point, used during login
    public static void fetchUserByEmail(String email, UserFetchCallback callback) {
        db.collection("users").whereEqualTo("Email", email).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String name = doc.getString("Name");
                    String role = doc.getString("Role");

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
        }).addOnFailureListener(e -> callback.onError("Error fetching user: " + e.getMessage()));
    }

    // This method can only be used when the Firebase instance was invoked by the admin
    public static void fetchAllUsers(UserListCallback callback) {
        db.collection("users").get().addOnSuccessListener(querySnapshot -> {
            List<Map<String, String>> userList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Map<String, String> userMap = new HashMap<>();
                userMap.put("Name", doc.getString("Name"));
                userMap.put("Email", doc.getString("Email"));
                userMap.put("Role", doc.getString("Role"));
                userList.add(userMap);
            }
            callback.onUserListFetched(userList);
        }).addOnFailureListener(e -> {
            callback.onError("Failed to load users: " + e.getMessage());
        });
    }

    public static void deleteUser(String email, UserFetchCallback callback) {
        db.collection("users").whereEqualTo("Email", email).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String docId = doc.getId();
                    db.collection("users").document(docId).delete().addOnSuccessListener(aVoid -> {
                        callback.onSuccess(null);
                    }).addOnFailureListener(e -> {
                        callback.onError("Error deleting user: " + e.getMessage());
                    });
                    break;
                }
            } else {
                callback.onError("No user found with email: " + email);
            }
        }).addOnFailureListener(e -> {
            callback.onError("Error finding user: " + e.getMessage());
        });
    }

    //NEW: Disable user by setting "disabled" to true
    public static void disableUser(String email, UserFetchCallback callback) {
        db.collection("users").whereEqualTo("Email", email).get().addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    doc.getReference().update("disabled", true)
                            .addOnSuccessListener(aVoid -> {
                                User user = doc.toObject(User.class);
                                callback.onSuccess(user);
                            })
                            .addOnFailureListener(e -> {
                                callback.onError("Failed to disable user: " + e.getMessage());
                            });
                    break;
                }
            } else {
                callback.onError("No user found with email: " + email);
            }
        }).addOnFailureListener(e -> {
            callback.onError("Error finding user: " + e.getMessage());
        });
    }

    public static void fetchAllCategories(CategoryListCallback callback) {
        db.collection("categories").get().addOnSuccessListener(querySnapshot -> {
            List<Map<String, String>> categoryList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Map<String, String> categoryMap = new HashMap<>();
                categoryMap.put("Name", doc.getString("Name"));
                categoryMap.put("Description", doc.getString("Description"));
                categoryList.add(categoryMap);
            }
            callback.onCategoryListFetched(categoryList);
        }).addOnFailureListener(e -> {
            callback.onError("Failed to load categories: " + e.getMessage());
        });
    }

    public static void deleteCategory(String name, FirebaseCallback callback) {
        db.collection("categories").whereEqualTo("Name", name).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            doc.getReference().delete()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onError("Error deleting category: " + e.getMessage()));
                        }
                    } else {
                        callback.onError("No category found with name: " + name);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error searching for category: " + e.getMessage());
                });
    }

    public static void editCategory(String oldName, String newName, String newDescription, FirebaseCallback callback) {
        db.collection("categories").whereEqualTo("Name", oldName).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("Name", newName);
                            updates.put("Description", newDescription);

                            doc.getReference().update(updates)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onError("Update failed: " + e.getMessage()));
                        }
                    } else {
                        callback.onError("Category not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error finding category: " + e.getMessage()));
    }

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
