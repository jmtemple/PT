package com.example.proyectotiti;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

public class structuresCon extends AppCompatActivity {

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String structureNum;
    private String visitNum;

    // Views
    //private EditText structure_name;



    private Spinner spinnerCondition;
    private EditText otherTextEdit;
    private EditText otherTextEditConstruction;
    private EditText structure_size;
    private EditText structure_function;
    private Switch structureCompliant;
    private EditText structureCompliantText;
    private TextView otherTextView;
    private TextView otherTextViewConstruction;
    private Spinner spinnerConstruction;

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
        setContentView(R.layout.activity_structures_con);

        // Get current info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        structureNum = extrasBundle.getString("structureNum");
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits");

        //Views
        //structure_name = (EditText)findViewById(R.id.editTextConstruccion);
        structure_size = (EditText)findViewById(R.id.editTextArea);
        structure_function = (EditText)findViewById(R.id.editTextFunction);


        structureCompliant = (Switch)findViewById(R.id.switch1);
        structureCompliantText = (EditText)findViewById(R.id.editTextCompliance);


        otherTextView = (TextView) findViewById(R.id.otherTextView);
        otherTextEdit = (EditText) findViewById(R.id.otherTextEdit);

        Yarumo = (CheckBox) findViewById(R.id.checkboxYarumo);
        Roja = (CheckBox) findViewById(R.id.checkboxRoja);
        Blanca = (CheckBox) findViewById(R.id.checkboxBlanca);
        Carreto = (CheckBox) findViewById(R.id.checkboxCarreto);
        Guasimo = (CheckBox) findViewById(R.id.checkboxGuasimo);
        Roble = (CheckBox) findViewById(R.id.checkboxRoble);
        Cana = (CheckBox) findViewById(R.id.checkboxCana);
        Bambu = (CheckBox) findViewById(R.id.checkboxBambu);
        Otro = (CheckBox) findViewById(R.id.checkboxOtro);


        spinnerConstruction = (Spinner) findViewById(R.id.spinnerConstruccion);
        spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);
        otherTextViewConstruction = (TextView) findViewById(R.id.otherTextViewConstruction);
        otherTextEditConstruction = (EditText) findViewById(R.id.otherTextEditConstruction);

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


        setUpTypeSpinner();

        spinnerConstruction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("Otro")){
                    otherTextEditConstruction.setVisibility(View.VISIBLE);
                    otherTextViewConstruction.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });





        // Set up onitemlistener to check if the user choses "other"



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
        mDatabase.child("visit"+visitNum).child("structures").child("construction").addValueEventListener(structureListener);
    }

    private void setUpTypeSpinner() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("new_construction");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("new_construction", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(structuresCon.this, android.R.layout.simple_spinner_item, types)
                {

                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerConstruction.setAdapter(typesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        mDatabase.child("visit"+visitNum).child("structures").child("construction").child("s_"+structureNum).addListenerForSingleValueEvent(sListener);
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
            otherTextEdit.setText(otherTxt);
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
            new_structure = new StructureDesc((structureType+" " + otherTextEdit.getText().toString()), structure_function.getText().toString(),spinnerConstruction.getSelectedItem().toString(), spinnerCondition.getSelectedItem().toString(), true, "",structure_size.getText().toString(), structureCompliant.isChecked(), structureCompliantText.getText().toString());

            // Add new option to the database
            //DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("structure_types").child(structureOther.getText().toString());
            //nDatabase.setValue(structureOther.getText().toString());
        }
        else{
            new_structure = new StructureDesc((structureType+" " + otherTextEdit.getText().toString()), structure_function.getText().toString(),spinnerConstruction.getSelectedItem().toString(), spinnerCondition.getSelectedItem().toString(), true, "",structure_size.getText().toString(), structureCompliant.isChecked(), structureCompliantText.getText().toString());
        }


        mDatabase.child("visit"+visitNum).child("structures").child("construction").child("s_"+structureNum).setValue(new_structure);
        openStructuresHome(v);
    }

    public void openStructuresHome(View v){
        Intent intentDetails = new Intent(structuresCon.this, structuresHome.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void checkone(View view) {
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
            otherTextEdit.setVisibility(View.VISIBLE);
            otherTextView.setVisibility(View.VISIBLE);
            addNewOption = true;

        }
        if(Otro.isChecked() != true) {
            otherTextEdit.setVisibility(View.GONE);
            otherTextView.setVisibility(View.GONE);
            otherTextEdit.setText("");
        }



    }
}
