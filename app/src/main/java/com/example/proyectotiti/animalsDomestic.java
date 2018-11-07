package com.example.proyectotiti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.DomesticAnimalDesc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.firebase.storage.FirebaseStorage.*;

public class animalsDomestic extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    // Passed from last screen
    private String familyNum;
    private String animalNum;
    private String visitNum;

    // Photo capability
    private ImageButton mImageButton;
    private Uri photoURI;
    private ArrayList<String> uris = new ArrayList<String>() {};
    private Map<String, String> images;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final  int REQUEST_DELETE = 2;
    private StorageReference filePath;
    private String path;


    // Views
    private LinearLayout mainLinearLayout;
    private EditText animalName;
    //private EditText animalMarking;
    private Switch animalCompliant;
    private EditText animalCompliantText;
    private EditText animalOther;
    private Spinner typeSpinner;
    //private EditText animalObservations;

    private long animalsCount;
    private boolean addNewOption;
    private DomesticAnimalDesc animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animals_domestic);


        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        animalNum = extrasBundle.getString("animalNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits");

        //Views
        animalName = (EditText)findViewById(R.id.editTextAnimal);
        //animalMarking = (EditText)findViewById(R.id.editTextMarking);
        animalCompliant = (Switch)findViewById(R.id.switch1);
        animalCompliantText = (EditText)findViewById(R.id.editTextCompliance);
        typeSpinner = (Spinner) findViewById(R.id.spinnerType);
        final TextView animalOtherTextView = (TextView) findViewById(R.id.textViewWildAnimalsOther);
        animalOther = (EditText)findViewById(R.id.editTextOther);
        //animalObservations = (EditText)findViewById(R.id.editTextObservations);

        mainLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);
        mImageButton = (ImageButton)findViewById(R.id.imageButton);
        storageReference = getInstance().getReference();


        // When the photo button is pressed, app will switch to android camera
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //doneImageView = (ImageView)findViewById(R.id.doneImageView);


        setUpTypeSpinner();

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String chosenOption = (String) parent.getItemAtPosition(position);
                Log.e("DEBUG", chosenOption);
                if (chosenOption.equals("otro")){
                    animalOther.setVisibility(View.VISIBLE);
                    animalOtherTextView.setVisibility(View.VISIBLE);
                    addNewOption = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        if (animalNum.equals("-1")){
            getAnimalNumber();
        }
        else {
            readFromDB();
        }
    }



    // Pulls the types of domestic animals from the Firebase database
    private void setUpTypeSpinner() {
        DatabaseReference tDatabase = FirebaseDatabase.getInstance().getReference().child("domestic_animals");
        tDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> types = new ArrayList<String>();

                for (DataSnapshot typeSnapshot: dataSnapshot.getChildren()) {
                    String typeName = typeSnapshot.getValue(String.class);
                    Log.d("domestic_animal", typeName);
                    types.add(typeName);
                }

                ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(animalsDomestic.this, android.R.layout.simple_spinner_item, types);
                typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(typesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /* This function runs upon the pressing of the camera button.
     * It will set up a new photo file and put the new photo into there.*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.proyectotiti.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg"         /* suffix */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /* This function runs upon completing taking a photo.
     *   It will upload the file to storage and add the uri to an array.*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            Uri uri = photoURI;

            //getting the storage reference
            filePath = storageReference.child("Photos").child(uri.getLastPathSegment());

            //adding the file to reference

            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //done icon visible
                            //doneImageView.setVisibility(View.VISIBLE);


                            // Add uri to list of new uris
                            uris.add(taskSnapshot.getDownloadUrl().toString());

                            mImageButton.setVisibility(View.GONE);

                            //Display new image
                          path = taskSnapshot.getDownloadUrl().toString();
                          ImageView image = new ImageView(animalsDomestic.this);
                          Picasso.with(image.getContext()).load(taskSnapshot.getDownloadUrl().toString()).resize(200,200).into(image);
                          mainLinearLayout.addView(image);
                          image.setOnClickListener(thumbnailClick);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

         if(requestCode == REQUEST_DELETE && resultCode == REQUEST_DELETE) {
             final StorageReference store = FirebaseStorage.getInstance().getReferenceFromUrl(path);
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("¿Por qué quieres eliminar?");

            EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);

             // Set up the buttons
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {




                             store.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                     Log.d("something", "On sucsess deleated file");


                     mImageButton.setVisibility(View.VISIBLE);
                     images = null;

                     String id = "a_" + animalNum;


                     DomesticAnimalDesc new_animal;
                     if(addNewOption) {
                         new_animal = new DomesticAnimalDesc(animalOther.getText().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString());
                     }else {
                         new_animal = new DomesticAnimalDesc(typeSpinner.getSelectedItem().toString(), animalName.getText().toString(), true, "", images, animalCompliant.isChecked(), animalCompliantText.getText().toString());
                     }
                     mDatabase.child("visit"+visitNum).child("animals").child("domestic").child(id).setValue(new_animal);

                     finish();
                    startActivity(getIntent());

                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.d("something", "error");
                 }
             });

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
    }

    /* This function runs once the family count has been read from the database.*/
    public void getAnimalNumber(){
        // Add value event listener to find the family number
        final ValueEventListener animalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                animalsCount = dataSnapshot.getChildrenCount();
                animalNum = String.valueOf((int)animalsCount + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("animals").child("domestic").addValueEventListener(animalListener);
    }

    // Pulls data from Firebase database
    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener aListener = new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                DomesticAnimalDesc post = dataSnapshot.getValue(DomesticAnimalDesc.class);
                prepopulate(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("visit"+visitNum).child("animals").child("domestic").child("a_"+animalNum).addListenerForSingleValueEvent(aListener);
    }

    public void prepopulate(DomesticAnimalDesc post) {
        animal = post;
        // Set all the editTexts to original data
        //animalMarking.setText(String.valueOf(animal.marking));
        animalName.setText(animal.name);
        for (int i=0;i<typeSpinner.getCount();i++){
            if (typeSpinner.getItemAtPosition(i).equals(post.type)){
                typeSpinner.setSelection(i);
            }
        }


        animalCompliantText.setText(animal.compliant_desc);
        animalCompliant.setChecked(animal.compliant);
        //animalObservations.setText(animal.observation_desc);
        //Map<String, String> image_object = animal.images;
        images = animal.images;
        Iterator it = null;

        // Display all saved images
        if (images!=null){
            it = images.entrySet().iterator();
            mImageButton.setVisibility(View.GONE);

            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ImageView image = new ImageView(animalsDomestic.this);
                path = pair.getValue().toString();
                Picasso.with(image.getContext()).load(pair.getValue().toString()).resize(200,200).into(image);

                mainLinearLayout.addView(image);
                image.setOnClickListener(thumbnailClick);

            }

        }

    }

    // Run when the user submits the animal
    public void submitAnimal(View v){
        Map<String, String> uploads = new HashMap<String, String>();

        for (String uri : uris){
            String uploadId = mDatabase.push().getKey();
            //creating the upload object to store uploaded image details
            uploads.put(uploadId, uri);

            //adding an upload to firebase database
        }
        if(images !=null){
            images.putAll(uploads);
        }
        else{
            images = uploads;
        }

        String id = "a_" + animalNum;

        DomesticAnimalDesc new_animal;
        if(addNewOption){
            new_animal = new DomesticAnimalDesc(animalOther.getText().toString(), animalName.getText().toString(), true, "",images, animalCompliant.isChecked(), animalCompliantText.getText().toString());

            // Add the new domestic animal option to the database
            DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("domestic_animals").child(animalOther.getText().toString());
            nDatabase.setValue(animalOther.getText().toString());
        }
        else{
            new_animal = new DomesticAnimalDesc(typeSpinner.getSelectedItem().toString(), animalName.getText().toString(), true, "",images, animalCompliant.isChecked(), animalCompliantText.getText().toString());

        }
        mDatabase.child("visit"+visitNum).child("animals").child("domestic").child(id).setValue(new_animal);

        openAnimalsHome(v);
    }

    public void openAnimalsHome(View v){
        Intent intentDetails = new Intent(animalsDomestic.this, animalsHome.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }


    private  View.OnClickListener thumbnailClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {

                Intent i = new Intent(animalsDomestic.this, cameraOptions.class);
                Bundle bundle = new Bundle();
                bundle.putString("Reimage", path);
                i.putExtras(bundle);

                startActivityForResult(i,REQUEST_DELETE);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };
}
