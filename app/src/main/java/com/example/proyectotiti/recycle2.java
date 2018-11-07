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

import com.example.proyectotiti.models.Recycle;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class recycle2 extends AppCompatActivity {

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
    private EditText recycle_items;
   // private EditText recycle_deliver;
    private Spinner  spinnerRecycle;
    private EditText editTextOther;
    private boolean addNewOption = false;

    private Recycle recycle;
    private Class nextField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle2);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        // Views
        recycle_items = (EditText) findViewById(R.id.editTextRecycled);
        //recycle_deliver = (EditText) findViewById(R.id.editTextRecyclingQ3);

        spinnerRecycle = (Spinner) findViewById(R.id.spinnerRecycle);
        final TextView textViewOther = (TextView) findViewById(R.id.textViewOther);
        editTextOther = (EditText)findViewById(R.id.editTextOther);


        setUpTypeSpinner();

        // Set up onitemlistener to check if the user choses "other"
        spinnerRecycle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);

                if (chosenOption.equals("Otro")){
                    editTextOther.setVisibility(View.VISIBLE);
                    textViewOther.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        readFromDB();

    }

    private void setUpTypeSpinner() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("people_action");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("people_action", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> recycleAdapter = new ArrayAdapter<String>(recycle2.this, android.R.layout.simple_spinner_item, types);
                recycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRecycle.setAdapter(recycleAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener rListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);

                if(post != null){
                    if(post.recycle.recycle_deliver != null && post.recycle.recycle_items != null){
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
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(rListener);
    }

    public void prepopulate(Recycle post){
        recycle = post;
        recycle_items.setText(recycle.recycle_items);
        //recycle_deliver.setText(recycle.recycle_deliver);'

       Boolean isOther = true;

        for (int i=0;i<spinnerRecycle.getCount();i++){
           if (spinnerRecycle.getItemAtPosition(i).equals(post.recycle_deliver)){
                spinnerRecycle.setSelection(i);
                isOther = false;
            }
        }

        if(isOther){

         spinnerRecycle.setSelection(4);
         editTextOther.setText(post.recycle_deliver);
        }

    }

    public void submitRecycle(View v){

        if(addNewOption){

            mDatabase.child("recycle").child("recycle_items").setValue(recycle_items.getText().toString());
            mDatabase.child("recycle").child("recycle_deliver").setValue(editTextOther.getText().toString());


            // Add the new wild animal option to the database
            //DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("stove_types").child(editTextOther.getText().toString());
           // nDatabase.setValue(editTextOther.getText().toString());
        }
        else{

            mDatabase.child("recycle").child("recycle_items").setValue(recycle_items.getText().toString());
            mDatabase.child("recycle").child("recycle_deliver").setValue(spinnerRecycle.getSelectedItem().toString());


        }

        openNextField(v);
    }

    public void openRecycle1(View v){

        Intent intentDetails = new Intent(recycle2.this, recycle1.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openNextField(View v){
        Intent intentDetails = new Intent(recycle2.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
