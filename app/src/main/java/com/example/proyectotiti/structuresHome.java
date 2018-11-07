package com.example.proyectotiti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.proyectotiti.models.Structure;
import com.example.proyectotiti.models.StructureDesc;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class structuresHome extends AppCompatActivity {

    private static final String TAG = "structuresHome";

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
    private Switch compliant_switch;
    private RadioGroup conRdGp;
    private RadioGroup fenceRdGp;
    private LinearLayout conLinearLayout;
    private LinearLayout fenceLinearLayout;
    private EditText input;

    // Holds the next screen
    private Class nextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structures_home);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        // Views
        compliant_switch = (Switch) findViewById(R.id.switch1);

        // Set up Radio Button Gorups with Linear Layout
        conLinearLayout = (LinearLayout) findViewById(R.id.linear_con);
        fenceLinearLayout = (LinearLayout) findViewById(R.id.linear_fence);
        conRdGp = new RadioGroup(this);
        fenceRdGp = new RadioGroup(this);
        conRdGp.setOrientation(RadioGroup.VERTICAL);
        fenceRdGp.setOrientation(RadioGroup.VERTICAL);

        // Fetch data
        readFromDB();
    }

    public void readFromDB() {

        // Add value event listener to the list of families
        ValueEventListener bdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);
                if(post != null){
                    // Prepopulate page
                    prepopulate(post.structures);

                    // Find the previous screen
                    if(post.animals.committed){
                        nextField = animalsHome.class;
                    }
                    else{
                        nextField= basicData.class;
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

        // Set views
        conLinearLayout.addView(conRdGp);
        fenceLinearLayout.addView(fenceRdGp);

    }

    // Prepopulate with data from Firebase
    public void prepopulate(Structure structure){

        // Set compliance switch
        compliant_switch.setChecked(structure.compliant);

        // Iterate through constructions and prepopulate
        if(structure.construction != null){

            for (Map.Entry<String, StructureDesc> entry : structure.construction.entrySet()) {

                String key = entry.getKey();
                StructureDesc value = entry.getValue();

                if (value != null) {
                    // Add as radio button
                    addConRadioButton(key,value);
                }
            }
        }

        // Iterate through fences and prepopulate
        if(structure.fence != null) {

            for (Map.Entry<String, StructureDesc> entry : structure.fence.entrySet()) {

                String key = entry.getKey();
                StructureDesc value = entry.getValue();

                if (value != null) {
                    // Add as radio button
                    addFenceRadioButton(key,value);
                }
            }
        }
    }

    // Add new construction as a radio button with the text as the construction name and the id as the id
    public void addConRadioButton(String key, StructureDesc value) {

        if(value.active){

            RadioButton rdbtn = new RadioButton(this);

            String[] s = key.split("_");
            int id = Integer.valueOf(s[1]);
            rdbtn.setId(id);

            rdbtn.setText(value.name);
            conRdGp.addView(rdbtn);
        }
    }

    // Add new fence as a radio button with the text as the fence name and the id as the id
    public void addFenceRadioButton(String key, StructureDesc value) {

        if(value.active){

            RadioButton rdbtn = new RadioButton(this);

            String[] s = key.split("_");
            int id = Integer.valueOf(s[1]);
            rdbtn.setId(id);

            rdbtn.setText(value.name);
            fenceRdGp.addView(rdbtn);
        }
    }

    public void deleteConStructure(View v){

        // Create popup message for why you are deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Por qué quieres eliminar?");

        input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int selectedId = conRdGp.getCheckedRadioButtonId();
                Log.e(TAG, "Deleting construction number" + String.valueOf(selectedId));

                if(selectedId != -1){
                    String id = "s_" + selectedId;
                    DatabaseReference dDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum).child("structures").child("construction").child(id);
                    dDatabase.child("active").setValue(false);
                    dDatabase.child("inactive_desc").setValue(input.getText().toString());

                    // Restart screen
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void deleteFenceStructure(View v){
        // Create popup message for why you are deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Por qué quieres eliminar?");

        input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int selectedId = fenceRdGp.getCheckedRadioButtonId();
                Log.e(TAG, "Deleting fence number" + String.valueOf(selectedId));

                if(selectedId != -1){
                    String id = "s_" + selectedId;
                    DatabaseReference dDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum).child("structures").child("fence").child(id);
                    dDatabase.child("active").setValue(false);
                    dDatabase.child("inactive_desc").setValue(input.getText().toString());

                    // Restart screen
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void openStructuresCook(View v){
        mDatabase.child("structures").child("compliant").setValue(compliant_switch.isChecked());
        Intent intentDetails = new Intent(structuresHome.this, structuresCook.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void openStructuresCon(View v){

        mDatabase.child("structures").child("compliant").setValue(compliant_switch.isChecked());

        String selectedId = String.valueOf(conRdGp.getCheckedRadioButtonId());

        Intent intentDetails = new Intent(structuresHome.this, structuresCon.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        bundle.putString("structureNum", selectedId);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openStructuresFence(View v){

        mDatabase.child("structures").child("compliant").setValue(compliant_switch.isChecked());

        String selectedId = String.valueOf(fenceRdGp.getCheckedRadioButtonId());

        Intent intentDetails = new Intent(structuresHome.this, structuresFence.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        bundle.putString("structureNum", selectedId);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openLastField(View v){


        Intent intentDetails = new Intent(structuresHome.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }
}
