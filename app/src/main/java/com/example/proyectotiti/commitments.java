package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class commitments extends AppCompatActivity {

    private static final String TAG = "commitments";

    // Declare database reference
    private DatabaseReference mDatabase;

    // Passed variables
    private String familyNum;
    private String visitNum;

    private CheckBox animalCheckbox;
    private CheckBox structureCheckbox;
    private CheckBox recycleCheckbox;
    private CheckBox conservacionCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitments);

        animalCheckbox = (CheckBox)findViewById(R.id.animalesCheckbox);
        structureCheckbox = (CheckBox)findViewById(R.id.maderaCheckbox);
        recycleCheckbox = (CheckBox)findViewById(R.id.recycleCheckBox);
        conservacionCheckbox = (CheckBox)findViewById(R.id.conservacionCheckbox);


        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);


    }

    public void submitCommitments(View v){
        mDatabase.child("animals").child("committed").setValue(animalCheckbox.isChecked());
        mDatabase.child("structures").child("committed").setValue(structureCheckbox.isChecked());
        mDatabase.child("recycle").child("committed").setValue(recycleCheckbox.isChecked());
        mDatabase.child("conservation").child("committed").setValue(conservacionCheckbox.isChecked());

        Intent intentDetails;
        if (animalCheckbox.isChecked()){
            intentDetails = new Intent(commitments.this, animalsHome.class);

        }
        else if (structureCheckbox.isChecked()){
            intentDetails = new Intent(commitments.this, structuresHome.class);

        }
        else if(recycleCheckbox.isChecked()){
            intentDetails = new Intent(commitments.this, recycle1.class);

        }
        else if(conservacionCheckbox.isChecked()){
            intentDetails = new Intent(commitments.this, conservation.class);

        }
        else{
            intentDetails = new Intent(commitments.this, visitOverview.class);

        }
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }

    public void openBasicData(View v){
        Intent intentDetails = new Intent(commitments.this, basicData.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
