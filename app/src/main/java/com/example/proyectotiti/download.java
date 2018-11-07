package com.example.proyectotiti;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;

public class download extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
    }

    public void openHome(View v){
        startActivity(new Intent(download.this, home.class));
    }
    public void openContinue(View v){
        startActivity(new Intent(download.this, continuePage.class));
    }
}
        // Is the view now checked?
        /*
        boolean checked = ((checkBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox_family1:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family2:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family3:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family4:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family5:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family6:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family7:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family8:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family9:
                if (checked)
                // download
                else
                // don't download
                break;
            case R.id.checkBox_family10:
                if (checked)
                // download
                else
                // don't download
                break;
        }
        */
