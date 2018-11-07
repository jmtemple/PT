package com.example.proyectotiti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.example.proyectotiti.models.Animal;
import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.DomesticAnimalDesc;
import com.example.proyectotiti.models.Visit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class animalsHome extends BaseActivity {

    private static final String TAG = "animalsHome";

    private DatabaseReference mDatabase;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Views
    private Switch compliant_switch;
    private RadioGroup wildRdGp;
    private RadioGroup domesticRdGp;
    private LinearLayout wildLinearLayout;
    private LinearLayout domesticLinearLayout;
    private EditText input;

    // Holds the next screen
    private Class nextField;

    /* This function runs upon the creation of the animalsHome screen.
    * If it is not an initial visit, it will prompt the app to read from the database
    * and prepopulate the text boxes.  Otherwise, it will prepopulate the family number
    * with the next consecutive number*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_home);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits");

        // Views
        compliant_switch = (Switch) findViewById(R.id.compliantSwitch);

        // Set up Radio Button Groups with Linear Layout
        wildLinearLayout = (LinearLayout) findViewById(R.id.linear_wild);
        domesticLinearLayout = (LinearLayout) findViewById(R.id.linear_domestic);
        wildRdGp = new RadioGroup(this);
        domesticRdGp = new RadioGroup(this);
        wildRdGp.setOrientation(RadioGroup.VERTICAL);
        domesticRdGp.setOrientation(RadioGroup.VERTICAL);

        // Fetch data
        readFromDB();


    }

    // Fetch data from Firebase database
    public void readFromDB() {

        // Add value event listener to the list of families
        ValueEventListener visitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);
                if(post != null){
                    // Prepopulate page
                    prepopulate(post.animals);

                    // Find the next screen
                    if(post.structures.committed){
                        nextField = structuresHome.class;
                    }
                    else if(post.recycle.committed){
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.child("visit"+visitNum).addValueEventListener(visitListener);

        // Set views
        wildLinearLayout.addView(wildRdGp);
        domesticLinearLayout.addView(domesticRdGp);

    }

    // Prepopulate with data from Firebase
    public void prepopulate(Animal animal){

        // Set compliance switch
        compliant_switch.setChecked(animal.compliant);

        // Iterate through wild animals and prepopulate
        if(animal.wild != null){

            for (Map.Entry<String, AnimalDesc> entry : animal.wild.entrySet()) {

                String key = entry.getKey();
                AnimalDesc value = entry.getValue();

                if (value != null) {
                    // Add as radio button
                    addWildRadioButton(key,value);
                }

            }
        }
        // Iterate through domestic animals and prepopulate
        if(animal.domestic != null) {

            for (Map.Entry<String, DomesticAnimalDesc> entry : animal.domestic.entrySet()) {

                String key = entry.getKey();
                DomesticAnimalDesc value = entry.getValue();

                if (value != null) {
                    // Add as radio button
                    addDomesticRadioButton(key,value);
                }

            }
        }
    }

    // Add new animal as a radio button with the text as the animal name and the id as the id
    public void addWildRadioButton(String key, AnimalDesc value) {

        if(value.active){

            RadioButton rdbtn = new RadioButton(this);

            String[] s = key.split("_");
            int id = Integer.valueOf(s[1]);
            rdbtn.setId(id);

            rdbtn.setText(value.name);
            wildRdGp.addView(rdbtn);
        }
    }

    // Add new animal as a radio button with the text as the animal name and the id as the id
    public void addDomesticRadioButton(String key, DomesticAnimalDesc value) {

        if(value.active){

            RadioButton rdbtn = new RadioButton(this);

            String[] s = key.split("_");
            int id = Integer.valueOf(s[1]);
            rdbtn.setId(id);

            rdbtn.setText(value.name);
            domesticRdGp.addView(rdbtn);
        }
    }

    // Called if the user is deleting a wild animal
    public void deleteWildAnimal(View v){

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

                int selectedId = wildRdGp.getCheckedRadioButtonId();
                Log.e(TAG, "Deleting wild animal number" + String.valueOf(selectedId));

                if(selectedId != -1){
                    String id = "a_" + selectedId;
                    DatabaseReference dDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum).child("animals").child("wild").child(id);
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

    // Called if the user is deleting a domestic animal
    public void deleteDomAnimal(View v){

        // Create popup message for why you are deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Por qué quieres eliminar?");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int selectedId = domesticRdGp.getCheckedRadioButtonId();
                Log.e(TAG, "Deleting domestic animal number" + String.valueOf(selectedId));

                if(selectedId != -1){
                    String id = "a_" + selectedId;
                    DatabaseReference dDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum).child("animals").child("domestic").child(id);
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

    // This is called if the back button is clicked
    public void openBasicData(View v){
        Log.e(TAG, "Opening basic data");
        Intent intentDetails = new Intent(animalsHome.this, basicData.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    // This is called if the continue button is clicked
    public void openNextField(View v){
        // Send compliant value to database
        mDatabase.child("visit"+visitNum).child("animals").child("compliant").setValue(compliant_switch.isChecked());

        Intent intentDetails = new Intent(animalsHome.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }

    // This is called if the user is editing or creating a new wild animal
    public void openAnimalsWild(View v){
        // Send compliant value to database
        mDatabase.child("visit"+visitNum).child("animals").child("compliant").setValue(compliant_switch.isChecked());

        int selectedId = wildRdGp.getCheckedRadioButtonId();

        Intent intentDetails = new Intent(animalsHome.this, animalsWild.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        bundle.putString("animalNum", String.valueOf(selectedId));
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }

    // This is called if the user is editing or creating a new domestic animal
    public void openAnimalsDomestic(View v){
        // Send compliant value to database
        mDatabase.child("visit"+visitNum).child("animals").child("compliant").setValue(compliant_switch.isChecked());

        int selectedId = domesticRdGp.getCheckedRadioButtonId();

        Intent intentDetails = new Intent(animalsHome.this, animalsDomestic.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        bundle.putString("animalNum", String.valueOf(selectedId));
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
}
