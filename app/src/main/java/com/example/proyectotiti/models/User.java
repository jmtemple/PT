package com.example.proyectotiti.models;

import com.google.firebase.database.IgnoreExtraProperties;

/** User Class
 * This class contains the username and email for each user.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String id;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String id) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

}
