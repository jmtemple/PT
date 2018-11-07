package com.example.proyectotiti.models;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This enables the database to be accessed while the user is offline.
 */

public class ProyectoTiti extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
