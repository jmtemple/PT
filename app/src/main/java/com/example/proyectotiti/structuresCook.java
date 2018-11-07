package com.example.proyectotiti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.Structure;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class structuresCook extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    // Passed from last screen
    private String familyNum;
    private String visitNum;

    // Photo capability
    private ImageButton mImageButton;
    private Uri photoURI;
    private ArrayList<String> uris = new ArrayList<String>() {};
    private Map<String, String> images;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final  int REQUEST_DELETE = 2;
    //private StorageReference filePath;
    private String path;


    // Views
    private LinearLayout mainLinearLayout;
    private RadioButton radioButtonSiWood;
    private RadioButton radioButtonNoWood;
    private RadioButton radioButtonSiCoal;
    private RadioButton radioButtonNoCoal;

    private Spinner spinnerLenaPurpose;
    private Spinner spinnerCarbonPurpose;
    private TextView carbonTextView;
    private TextView lenaTextView;

    private Structure structure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structures_cook);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");

        mainLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);
        mImageButton = (ImageButton)findViewById(R.id.imageButtonCooking);
        storageReference = FirebaseStorage.getInstance().getReference();

        // When the photo button is pressed, app will switch to android camera
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Views
        radioButtonSiWood = (RadioButton) findViewById(R.id.radioButtonSiWood);
        radioButtonNoWood = (RadioButton) findViewById(R.id.radioButtonNoWood);
        radioButtonSiCoal = (RadioButton) findViewById(R.id.radioButtonSiCoal);
        radioButtonNoCoal = (RadioButton) findViewById(R.id.radioButtonNoCoal);

        spinnerLenaPurpose = (Spinner) findViewById(R.id.spinnerPurpose1);
        ArrayAdapter<CharSequence> lenaAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerPurposes, android.R.layout.simple_spinner_item);
        lenaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLenaPurpose.setAdapter(lenaAdapter);

        spinnerCarbonPurpose = (Spinner) findViewById(R.id.spinnerPurpose2);
        ArrayAdapter<CharSequence> carbonAdapter = ArrayAdapter.createFromResource(this,R.array.spinnerPurposes, android.R.layout.simple_spinner_item);
        carbonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarbonPurpose.setAdapter(carbonAdapter);

        carbonTextView = (TextView) findViewById(R.id.textViewPurpose2);
        lenaTextView = (TextView) findViewById(R.id.textViewPurpose1);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum).child("structures");
        if (!visitNum.equals("1")) {
            readFromDB();
        }

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
            StorageReference filePath = storageReference.child("Photos").child(uri.getLastPathSegment());

            //adding the file to reference
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            // Add uri to list of new uris
                            uris.add(taskSnapshot.getDownloadUrl().toString());

                            mImageButton.setVisibility(View.GONE);

                            //Display new image
                          path = taskSnapshot.getDownloadUrl().toString();
                          ImageView image = new ImageView(structuresCook.this);
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

                     mDatabase.child("images").setValue(images);
                     mDatabase.child("cookWithWood").setValue(radioButtonSiWood.isChecked());
                     mDatabase.child("cookWithCoal").setValue(radioButtonSiCoal.isChecked());

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

    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener sListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Structure post = dataSnapshot.getValue(Structure.class);
                prepopulate(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Family failed, log a message
                Log.w("DEBUG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(sListener);
    }

    public void prepopulate(Structure post){
        structure = post;
        if(post.cookWithWood){
            radioButtonSiWood.setChecked(true);
        }
        else{
            radioButtonNoWood.setChecked(true);
        }

        if(post.cookWithCoal){
            radioButtonSiCoal.setChecked(true);
        }
        else{
            radioButtonNoCoal.setChecked(true);
        }

        Map<String, String> image_object = structure.images;
        images = structure.images;
        Iterator it = null;


        // Display all saved images
        if (images!=null){
            it = images.entrySet().iterator();
            mImageButton.setVisibility(View.GONE);

            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ImageView image = new ImageView(structuresCook.this);
                path = pair.getValue().toString();
                Picasso.with(image.getContext()).load(pair.getValue().toString()).resize(200,200).into(image);

                mainLinearLayout.addView(image);
                image.setOnClickListener(thumbnailClick);

            }

        }
    }

    public void submitStructure(View v){
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
        mDatabase.child("images").setValue(images);

        mDatabase.child("cookWithWood").setValue(radioButtonSiWood.isChecked());
        mDatabase.child("cookWithCoal").setValue(radioButtonSiCoal.isChecked());
        if(radioButtonSiWood.isChecked() || radioButtonSiCoal.isChecked()){
            openStructuresCook2(v);
        }
        else {
            if(radioButtonNoWood.isChecked() && radioButtonNoCoal.isChecked()) {
                openRecycle1(v);
            }
        }
    }

    public void openStructuresHome(View v){
        Intent intentDetails = new Intent(structuresCook.this, structuresHome.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openStructuresCook2(View v){

        Intent intentDetails = new Intent(structuresCook.this, structuresCook2.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openRecycle1(View v){
        Intent intentDetails = new Intent(structuresCook.this, recycle1.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }

    public void carbonUso(View view) {

        if(radioButtonSiCoal.isChecked()){

            spinnerCarbonPurpose.setVisibility(View.VISIBLE);
            carbonTextView.setVisibility(View.VISIBLE);
        }


    }

    public void lenaUso(View view) {
        if(radioButtonSiWood.isChecked()){
            spinnerLenaPurpose.setVisibility(View.VISIBLE);
            lenaTextView.setVisibility(View.VISIBLE);
        }
    }

    public void lenaUsoNo(View view) {
        spinnerLenaPurpose.setVisibility(View.GONE);
        lenaTextView.setVisibility(View.GONE);
    }

    public void carbonUsoNo(View view) {
        spinnerCarbonPurpose.setVisibility(View.GONE);
        carbonTextView.setVisibility(View.GONE);
    }

    private  View.OnClickListener thumbnailClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {

                Intent i = new Intent(structuresCook.this, cameraOptions.class);
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
