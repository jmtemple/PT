package com.example.proyectotiti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.proyectotiti.R;
import com.example.proyectotiti.animalsWild;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class cameraOptions extends AppCompatActivity {

    private ImageButton deleteBtn;
    private Button doneBtn;
    private ImageView imageView;
    private static final  int REQUEST_DELETE = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_options);

        deleteBtn = (ImageButton) findViewById(R.id.deleteBtn);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        imageView = (ImageView) findViewById(R.id.imageView);


        Intent intentExtras = getIntent();
        Bundle extras = intentExtras.getExtras();

            String path = extras.getString("Reimage");
            Picasso.with(imageView.getContext()).load(path).into(imageView);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent();
                setResult(REQUEST_DELETE, resultIntent);
                finish();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}



