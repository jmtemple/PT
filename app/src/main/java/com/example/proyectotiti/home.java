package com.example.proyectotiti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends BaseActivity {

    private static final String TAG = "home";

    private DatabaseReference mDatabase;

    private String newFamilyNum;

    /* This function runs upon the creation of the home screen. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void getNextFamilyNum(){
        // Add value event listener to find the family number
        ValueEventListener familyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long families_count = dataSnapshot.getChildrenCount();
                Log.e(TAG, "Number of existing families: " + String.valueOf(families_count));
                families_count = families_count + 1;
                newFamilyNum = String.valueOf(families_count);

                // Pass the id of the family selected to the new activity
                Intent intentDetails = new Intent(home.this, basicData.class);
                Bundle bundle = new Bundle();
                bundle.putString("familyNum", newFamilyNum);
                bundle.putString("visitNum", "1");
                intentDetails.putExtras(bundle);
                startActivity(intentDetails);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(familyListener);
    }

    /* This function runs upon the clicking of the new family button.
     * Opens basic data for initial visits for families. */
    public void openBasicData(View v){
        // Get the next available id number for the family
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families");
        getNextFamilyNum();
    }

    /* This function runs upon the clicking of the continue family button.
     * Opens continue page to decide which family to follow up. */
    public void openContinue(View v){
        startActivity(new Intent(home.this, continuePage.class));
    }


    /* This function runs upon the clicking of the sign out  button. */
    public void openMain(View v){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(home.this, login.class));
        finish();
    }
}
