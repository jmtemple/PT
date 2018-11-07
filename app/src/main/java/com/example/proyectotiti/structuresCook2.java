package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.Structure;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class structuresCook2 extends AppCompatActivity {

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
   // private EditText stove_freq;
    //private EditText stove_type;

    private EditText otherStoveType;
    private Spinner spinnerFreq;
    private Spinner spinnerStoveType;

    private Structure structure;
    private Class nextField;
    private boolean addNewOption = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structures_cook2);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");

        // Views
        spinnerStoveType = (Spinner) findViewById(R.id.spinnerStoveType);
        final TextView otherTextView = (TextView) findViewById(R.id.textViewStoveType);
        otherStoveType = (EditText) findViewById(R.id.editTextStoveType);

        // stove_freq = (EditText) findViewById(R.id.editTextFreq);
        //stove_type = (EditText) findViewById(R.id.editTextStoveType);

        spinnerFreq = (Spinner) findViewById(R.id.spinnerFreq);

        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerFrequency, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq.setAdapter(freqAdapter);

        spinnerFreq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setUpTypeSpinner();


        // Set up onitemlistener to check if the user choses "other"
        spinnerStoveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);

                if (chosenOption.equals("Otro")) {
                    otherStoveType.setVisibility(View.VISIBLE);
                    otherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit" + visitNum);
        readFromDB();
    }

        // Pulls the types of wild animals from the Firebase database

        private void setUpTypeSpinner () {
            DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("stove_type");
            tDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    final List<String> types = new ArrayList<String>();

                    for (DataSnapshot typeSnapshot : dataSnapshot.getChildren()) {
                        String typeName = typeSnapshot.getValue(String.class);
                        Log.d("stove_type", typeName);
                        types.add(typeName);
                    }

                    ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(structuresCook2.this, android.R.layout.simple_spinner_item, types);
                    typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStoveType.setAdapter(typesAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    public void readFromDB(){

        // Add value event listener to the list of families
        ValueEventListener bdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("DEBUG", String.valueOf(dataSnapshot));
                Visit post = dataSnapshot.getValue(Visit.class);
                if(post != null){

                   if(post.structures.stove_freq != null && post.structures.stove_type != null){
                     prepopulate(post.structures);

                    }
                    
                    if(post.recycle.committed){
                        nextField = recycle1.class;
                    }
                    else if(post.conservation.committed){
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
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(bdListener);

    }

    public void prepopulate(Structure post){
        structure = post;
        boolean inSpinner = true;
        for (int i=0;i<spinnerFreq.getCount();i++){
          if (spinnerStoveType.getItemAtPosition(i).equals(post.stove_freq)){
               spinnerFreq.setSelection(i);
            }
        }

        for (int i=0;i<spinnerStoveType.getCount();i++){
            if (spinnerStoveType.getItemAtPosition(i).equals(post.stove_type)){
               spinnerStoveType.setSelection(i);
               inSpinner = false;
            }
        }
        if(inSpinner){
            spinnerStoveType.setSelection(4);
            otherStoveType.setText(post.stove_type);
        }

    }

    public void submitStructure(View v){

        if(addNewOption){

            mDatabase.child("structures").child("stove_freq").setValue(spinnerFreq.getSelectedItem().toString());
            mDatabase.child("structures").child("stove_type").setValue(otherStoveType.getText().toString());


            // Add the new wild animal option to the database
            //DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("stove_types").child(otherStoveType.getText().toString());
            //nDatabase.setValue(otherStoveType.getText().toString());
        }
        else{
            mDatabase.child("structures").child("stove_freq").setValue(spinnerFreq.getSelectedItem().toString());
            mDatabase.child("structures").child("stove_type").setValue(spinnerStoveType.getSelectedItem().toString());

        }

        openNextField(v);
    }

    public void openMadera4(View v){
        Intent intentDetails = new Intent(structuresCook2.this, structuresCook.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void openNextField(View v){
        Intent intentDetails = new Intent(structuresCook2.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }
}
