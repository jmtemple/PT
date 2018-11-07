package com.example.proyectotiti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class revise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
    }

    public void openHome(View v){
        startActivity(new Intent(revise.this, home.class));
    }
    public void openRecycle1(View v){
        startActivity(new Intent(revise.this, recycle1.class));
    }
}
