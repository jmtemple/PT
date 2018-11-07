package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.proyectotiti.models.Recycle;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class recycle3 extends AppCompatActivity {

    private static final String TAG = "recycle3";

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
    private EditText waste_man;

    private Recycle recycle;
    private Class nextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle3);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        // Views
        waste_man = (EditText) findViewById(R.id.editText2);

        readFromDB();
    }

    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);

                if(post != null){
                    if(post.recycle.waste_man != null){
                        prepopulate(post.recycle);

                    }
                    if(post.conservation.committed){
                        nextField = conservation.class;
                    }
                    else{
                        nextField = visitOverview.class;
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

    public void prepopulate(Recycle post){
        recycle = post;
        waste_man.setText(recycle.waste_man);

    }

    public void submitRecycle(View v){

        mDatabase.child("recycle").child("waste_man").setValue(waste_man.getText().toString());

        openNextField(v);
    }

    public void openRecycle1(View v){

        Intent intentDetails = new Intent(recycle3.this, recycle1.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openNextField(View v){
        Intent intentDetails = new Intent(recycle3.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
