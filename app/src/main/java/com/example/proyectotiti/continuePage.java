package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.proyectotiti.models.Family;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class continuePage extends AppCompatActivity {

    private static final String TAG = "continuePage";

    private RadioGroup familyRdbtn;

    private DatabaseReference mDatabase;

    private Map<Integer,String> families = new HashMap<>();

    /* This function runs upon the creation of the continue screen.
    * Sets up the radio button group for the families and a reference to the database
    * where the families are stored. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_page);

        // Set up Radio Button Group with Linear Layout
        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.familyLinearLayout);
        familyRdbtn = new RadioGroup(this);
        familyRdbtn.setOrientation(RadioGroup.VERTICAL);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families");

        mLinearLayout.addView(familyRdbtn);
    }

    /* This function runs upon the creation of the home screen.
    * It adds a value event listener to the database reference in order to find the families. */
    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the list of families
        ValueEventListener familyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot familySnapshot: dataSnapshot.getChildren()) {
                    String family_id = familySnapshot.getKey();
                    Family post = familySnapshot.getValue(Family.class);
                    if (post != null){
                        families.put(Integer.parseInt(family_id),post.name);
                    }
                }
                sortByFamilyName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(familyListener);
    }

    public void sortByFamilyName() {
        List<Integer> mapKeys = new ArrayList<Integer>(families.keySet());
        List<String> mapValues = new ArrayList<String>(families.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        Map<Integer, String> sortedMap = new HashMap<Integer,String>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                String comp1 = families.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    addFamilyRadioButton(key,val);
                    break;
                }
            }
        }

    }

    /* This function runs upon the finding of an existing family.
     * It will add the family as a radio button with the text as the family name and the id
      * as the id.*/
    public void addFamilyRadioButton(Integer key, String val) {
        RadioButton rdbtn = new RadioButton(this);

        rdbtn.setId(key);
        rdbtn.setText(val);
        familyRdbtn.addView(rdbtn);


    }


    /* This function runs upon the selection of the submit button.
     * It will pass the family number to the next screen. */
    public void openViewVisits(View v){
        int selectedId = familyRdbtn.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return;
        }

        // Pass the id of the family selected to the new activity
        // Pass false to initial visit flag
        // Pass true to firstPass
        Intent intentDetails = new Intent(continuePage.this, viewVisits.class);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", String.valueOf(selectedId));
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    /* This function runs upon clicking the back button. */
    public void openHome(View v){
        startActivity(new Intent(continuePage.this, home.class));
    }
}
