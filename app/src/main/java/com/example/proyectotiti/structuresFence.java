package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.proyectotiti.models.StructureDesc;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class structuresFence extends AppCompatActivity {

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String structureNum;
    private String visitNum;

    // Views
    private EditText structure_name;
    private EditText structureOther;
    private Spinner spinnerCondition;
    private EditText structure_size;
    private EditText structure_function;
    private Switch structureCompliant;
    private EditText structureCompliantText;
    private TextView structureOtherTextView;

    private CheckBox Yarumo;
    private CheckBox Roja;
    private CheckBox Blanca;
    private CheckBox Carreto;
    private CheckBox Guasimo;
    private CheckBox Roble;
    private CheckBox Cana;
    private CheckBox Bambu;
    private CheckBox Otro;

    private String structureType;

    private long structuresCount;
    private boolean addNewOption;
    private StructureDesc structure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structures_fence);

        // Get current info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        structureNum = extrasBundle.getString("structureNum");
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits");

        //Views
        structure_name = (EditText)findViewById(R.id.editTextCercado);


        Yarumo = (CheckBox) findViewById(R.id.checkboxYarumo);
        Roja = (CheckBox) findViewById(R.id.checkboxRoja);
        Blanca = (CheckBox) findViewById(R.id.checkboxBlanca);
        Carreto = (CheckBox) findViewById(R.id.checkboxCarreto);
        Guasimo = (CheckBox) findViewById(R.id.checkboxGuasimo);
        Roble = (CheckBox) findViewById(R.id.checkboxRoble);
        Cana = (CheckBox) findViewById(R.id.checkboxCana);
        Bambu = (CheckBox) findViewById(R.id.checkboxBambu);
        Otro = (CheckBox) findViewById(R.id.checkboxOtro);




        structure_size = (EditText)findViewById(R.id.editTextPerimeter);
        structure_function = (EditText)findViewById(R.id.editTextFunction);
        structureOtherTextView = (TextView) findViewById(R.id.textViewConStructureOther);
        structureOther = (EditText)findViewById(R.id.editTextOther);
        structureCompliant = (Switch)findViewById(R.id.switch1);
        structureCompliantText = (EditText)findViewById(R.id.editTextCompliance);

        spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);


        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerCondition, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(conditionAdapter);




        spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent , View view, int position, long l) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        if (structureNum.equals("-1")){
            getStructureNumber();
        }
        else {
            readFromDB();
        }
    }



    /* This function runs once the family count has been read from the database.*/
    public void getStructureNumber(){
        // Add value event listener to find the family number
        final ValueEventListener structureListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                structuresCount = dataSnapshot.getChildrenCount();
                structureNum = String.valueOf((int)structuresCount + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("structures").child("fence").addValueEventListener(structureListener);
    }

    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener sListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StructureDesc post = dataSnapshot.getValue(StructureDesc.class);
                prepopulate(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("structures").child("fence").child("s_"+structureNum).addListenerForSingleValueEvent(sListener);
    }

    public void prepopulate(StructureDesc post){
        structure = post;
        // Set all the editTexts to original data
       // structure_name.setText(structure.name);

        String otherTxt = post.type;
        if(post.type.contains(Yarumo.getText())) {

            Yarumo.setChecked(true);
            otherTxt = otherTxt.replace(Yarumo.getText(),"");
        }
        if(post.type.contains(Roja.getText())){
            Roja.setChecked(true);
            otherTxt = otherTxt.replace(Roja.getText(),"");

        }
        if(post.type.contains(Blanca.getText())){
            Blanca.setChecked(true);
            otherTxt = otherTxt.replace(Blanca.getText(),"");

        }
        if(post.type.contains(Carreto.getText())){
            Carreto.setChecked(true);
            otherTxt = otherTxt.replace(Carreto.getText(),"");

        }
        if(post.type.contains(Guasimo.getText())){
            Guasimo.setChecked(true);
            otherTxt = otherTxt.replace(Guasimo.getText(),"");

        }
        if(post.type.contains(Roble.getText())){
            Roble.setChecked(true);
            otherTxt = otherTxt.replace(Roble.getText(),"");

        }
        if(post.type.contains(Cana.getText())){
            Cana.setChecked(true);
            otherTxt = otherTxt.replace(Cana.getText(),"");

        }
        if(post.type.contains(Bambu.getText())){
            Bambu.setChecked(true);
            otherTxt = otherTxt.replace(Bambu.getText(),"");

        }
        if(!(otherTxt.equals("")|| otherTxt.equals(" "))){
            Otro.setChecked(true);
            structureOther.setText(otherTxt);
        }

        for (int i=0;i<spinnerCondition.getCount();i++){
            if (spinnerCondition.getItemAtPosition(i).equals(post.name)){
                spinnerCondition.setSelection(i);
            }
        }

        structure_function.setText(structure.function);
        structureCompliantText.setText(structure.compliant_desc);
        structureCompliant.setChecked(structure.compliant);
        structure_size.setText(structure.size);

    }

    public void submitStructure(View v){
        StructureDesc new_structure;
        if(addNewOption){
            new_structure = new StructureDesc((structureType+ " " + structureOther.getText().toString()), structure_function.getText().toString(), structure_name.getText().toString(), spinnerCondition.getSelectedItem().toString(), true, "",structure_size.getText().toString(), structureCompliant.isChecked(), structureCompliantText.getText().toString());

            // Add new option to the database
            DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("structure_types").child(structureOther.getText().toString());
            nDatabase.setValue(structureOther.getText().toString());
        }
        else{
            new_structure = new StructureDesc((structureType+ " " + structureOther.getText().toString()), structure_function.getText().toString(), structure_name.getText().toString(), spinnerCondition.getSelectedItem().toString(), true, "",structure_size.getText().toString(), structureCompliant.isChecked(), structureCompliantText.getText().toString());
        }
        mDatabase.child("visit"+visitNum).child("structures").child("fence").child("s_"+structureNum).setValue(new_structure);
        openStructuresHome(v);
    }

    public void openStructuresHome(View v){

        Intent intentDetails = new Intent(structuresFence.this, structuresHome.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void checktwo(View view) {

        structureType ="";

        if(Yarumo.isChecked()) {

            structureType = structureType + " " + Yarumo.getText().toString();

        }
        if(Roja.isChecked()){
            structureType = structureType + " " + Roja.getText().toString();

        }
        if(Blanca.isChecked()){
            structureType = structureType + " " + Blanca.getText().toString();

        }
        if(Carreto.isChecked()){
            structureType = structureType + " " + Carreto.getText().toString();

        }
        if(Guasimo.isChecked()){
            structureType = structureType + " " + Guasimo.getText().toString();

        }
        if(Roble.isChecked()){
            structureType = structureType + " " + Roble.getText().toString();

        }
        if(Cana.isChecked()){
            structureType = structureType + " " + Cana.getText().toString();

        }
        if(Bambu.isChecked()){
            structureType = structureType + " " + Bambu.getText().toString();

        }
        if(Otro.isChecked()){

            structureOther.setVisibility(View.VISIBLE);
            structureOtherTextView.setVisibility(View.VISIBLE);
            addNewOption = true;

        }

        if(Otro.isChecked() != true) {
            structureOther.setVisibility(View.GONE);
            structureOtherTextView.setVisibility(View.GONE);
            structureOther.setText("");
        }

    }
}
