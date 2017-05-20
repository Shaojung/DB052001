package com.example.teacher.db052001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE = 123;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        else
        {
            btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults.length > 0)
            {
                if (grantResults[0] == PERMISSION_GRANTED)
                {
                    btn.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn.setVisibility(View.INVISIBLE);
                }
            }

        }
    }
}
