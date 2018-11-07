package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.example.proyectotiti.models.Conservation;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class conservation extends AppCompatActivity {

    private static final String TAG = "conservation";

    private DatabaseReference mDatabase;


    private String familyNum;
    private String visitNum;

    private EditText area;
    private EditText agree_area;

    private Switch compliant_switch;


    private Class nextField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conservation);

        compliant_switch = (Switch) findViewById(R.id.switch1);
        area = (EditText) findViewById(R.id.editTextHectareNum);
        agree_area = (EditText) findViewById(R.id.editTextHectareAgrNum);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        readFromDB();
    }

    public void readFromDB() {


        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, String.valueOf(dataSnapshot));
                Visit post = dataSnapshot.getValue(Visit.class);
                if(post != null){
                    if(post.conservation.area != null || post.conservation.agree_area != null){
                        prepopulate(post.conservation);
                    }
                    if(post.recycle.committed){
                        nextField = recycle1.class;
                    }
                    else if(post.structures.committed){
                        nextField = structuresHome.class;
                    }
                    else if(post.animals.committed){
                        nextField = animalsHome.class;
                    }
                    else{
                        nextField = basicData.class;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(visitListener);

    }

    private void prepopulate(Conservation conservation) {
        area.setText(conservation.area);
        agree_area.setText(conservation.agree_area);
        compliant_switch.setChecked(conservation.compliant);
    }

    public void openLastField(View v){

        Intent intentDetails = new Intent(conservation.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);


    }

//    public void openConservacion0(View v){
//        // Pass the id of the family selected to the new activity
//        // Pass false to initial visit flag
//        Intent intentDetails = new Intent(conservation.this, conservacion0.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("familyNum", familyNum);
//        bundle.putString("visitNum", visitNum);
//        intentDetails.putExtras(bundle);
//        startActivity(intentDetails);
//    }

    public void openVisitOverview(View v){
        mDatabase.child("conservation").child("area").setValue(area.getText().toString());
        mDatabase.child("conservation").child("agree_area").setValue(agree_area.getText().toString());
        mDatabase.child("conservation").child("compliant").setValue(compliant_switch.isChecked());

        // Pass the id of the family selected to the new activity
        // Pass false to initial visit flag
        Intent intentDetails = new Intent(conservation.this, visitOverview.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
