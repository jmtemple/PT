package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.proyectotiti.models.Family;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class viewVisits extends AppCompatActivity {

    private static final String TAG = "viewVisits";

    private String familyNum;
    private String visitNum;
    private DatabaseReference mDatabase;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_visits);

        // Get Views
        mLinearLayout =(LinearLayout) findViewById(R.id.linearLayout);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");

        // Set up database reference at chosen family
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum);

    }

    /* This function runs upon the creation of the home screen.
* It adds a value event listener to the database reference in order to find the visits. */
    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot visitSnapshot: dataSnapshot.getChildren()) {
                    String visit_id = visitSnapshot.getKey();
                    Visit post = visitSnapshot.getValue(Visit.class);
                    if (post != null){
                        addVisit(visit_id, post);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visits").addListenerForSingleValueEvent(visitListener);
    }

    public void addVisit(String id, Visit visit){
        Button button = new Button(this);
        Integer visitId = Integer.parseInt(id.substring(5));
        button.setId(visitId);
        button.setText("visitar "+ visitId);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startVisitOverview(v);
            }
        });
        mLinearLayout.addView(button);
    }

    public void getNewVisitNumber(){
        // Add value event listener to find the visit number
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numOfVisits = dataSnapshot.getChildrenCount();
                numOfVisits = numOfVisits + 1;
                visitNum = String.valueOf(numOfVisits);

                // Pass the id of the family selected to the new activity
                Intent intentDetails = new Intent(viewVisits.this, basicData.class);
                Bundle bundle = new Bundle();
                bundle.putString("familyNum", familyNum);
                bundle.putString("visitNum", visitNum);
                intentDetails.putExtras(bundle);
                startActivity(intentDetails);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visits").addListenerForSingleValueEvent(visitListener);
    }

    public void startVisitOverview(View v){
        visitNum = String.valueOf(v.getId());
        Log.e(TAG, "Visit number: " + visitNum);
        Intent intentDetails = new Intent(viewVisits.this, visitOverview.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void startNewVisit(View v){
        // Get next visit number
        getNewVisitNumber();

    }

    public void openHome(View v){
        Intent intentDetails = new Intent(viewVisits.this, home.class);
        startActivity(intentDetails);
    }
}
