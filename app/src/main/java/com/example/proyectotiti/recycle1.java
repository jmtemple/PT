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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.proyectotiti.models.AnimalDesc;
import com.example.proyectotiti.models.Recycle;
import com.example.proyectotiti.models.Visit;
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

public class recycle1 extends AppCompatActivity {

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
    private StorageReference filePath;
    private String path;


    // Views
    private Switch compliant_switch;
    private LinearLayout mainLinearLayout;
    private RadioButton radioButtonSi;
    private RadioButton radioButtonNo;

    private Recycle recycle;
    private Class nextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle1);

        // Get current Info
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        familyNum = extrasBundle.getString("familyNum");
        visitNum = extrasBundle.getString("visitNum");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("families").child(familyNum).child("visits").child("visit"+visitNum);

        // Views
        radioButtonSi = (RadioButton) findViewById(R.id.radioButtonSi);
        radioButtonNo = (RadioButton) findViewById(R.id.radioButtonNo);
        compliant_switch = (Switch) findViewById(R.id.switch1);

        mainLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutMain);
        mImageButton = (ImageButton)findViewById(R.id.imageButtonMadera);
        storageReference = FirebaseStorage.getInstance().getReference();

        // When the photo button is pressed, app will switch to android camera
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        readFromDB();

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
                          ImageView image = new ImageView(recycle1.this);
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

                     mDatabase.child("recycle").child("compliant").setValue(compliant_switch.isChecked());
                     mDatabase.child("images").setValue(images);
                     if(radioButtonSi.isChecked()){
                         mDatabase.child("recycle").child("doRecycle").setValue(true);
                     }
                     else {
                         if(radioButtonNo.isChecked()) {
                             mDatabase.child("recycle").child("doRecycle").setValue(false);
                         }
                     }

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

    // Pulls data from Firebase database
    public void readFromDB(){
        // Add value event listener to the list of families
        ValueEventListener bdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Visit post = dataSnapshot.getValue(Visit.class);

                if(post != null){

                    if (!visitNum.equals("1")){
                        prepopulate(post.recycle);
                    }

                    if(post.structures.committed){
                        nextField = structuresHome.class;
                    }
                    else if(post.animals.committed){
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
    }

    public void prepopulate(Recycle post){
        // Set compliance switch
        compliant_switch.setChecked(post.compliant);
        recycle = post;
        if(post.doRecycle){
            radioButtonSi.setChecked(true);
        }
        else{
            radioButtonNo.setChecked(true);
        }

        //Map<String, String> image_object = recycle.images;
        images = recycle.images;
        Iterator it = null;

        // Display all saved images
        if (images!=null){
            it = images.entrySet().iterator();
            mImageButton.setVisibility(View.GONE);

            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                ImageView image = new ImageView(recycle1.this);
                path = pair.getValue().toString();
                Picasso.with(image.getContext()).load(pair.getValue().toString()).resize(200,200).into(image);

                mainLinearLayout.addView(image);
                image.setOnClickListener(thumbnailClick);

            }

        }

    }

    public void submitRecycle(View v){
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

        mDatabase.child("recycle").child("compliant").setValue(compliant_switch.isChecked());
        mDatabase.child("images").setValue(images);
        if(radioButtonSi.isChecked()){
            mDatabase.child("recycle").child("doRecycle").setValue(true);
            openRecycle2(v);
        }
        else {
            if(radioButtonNo.isChecked()) {
                mDatabase.child("recycle").child("doRecycle").setValue(false);
                openRecycle3(v);
            }
        }
    }

    public void openLastField(View v){
        Intent intentDetails = new Intent(recycle1.this, nextField);
        Bundle bundle = new Bundle();
        bundle.putString("familyNum", familyNum);
        bundle.putString("visitNum", visitNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);

    }
    public void openRecycle2(View v){

        Intent intentDetails = new Intent(recycle1.this, recycle2.class);
        Bundle bundle = new Bundle();
        bundle.putString("visitNum", visitNum);
        bundle.putString("familyNum", familyNum);
        intentDetails.putExtras(bundle);
        startActivity(intentDetails);
    }
    public void openRecycle3(View v){
        Intent intentDetails = new Intent(recycle1.this, recycle3.class);
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

                Intent i = new Intent(recycle1.this, cameraOptions.class);
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
