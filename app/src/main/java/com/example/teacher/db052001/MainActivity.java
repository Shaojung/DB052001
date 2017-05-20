package com.example.teacher.db052001;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE = 123;
    String targetAddr;
    BluetoothAdapter BTadapter;
    BluetoothLeScanner scanner;
    BluetoothDevice BTDevice;
    MyScanCallBack callBack = new MyScanCallBack();
    Handler hander;
    TextView tv1;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hander = new Handler();
        btn = (Button) findViewById(R.id.button);
        tv1 = (TextView) findViewById(R.id.textView);
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(it, 123);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK)
        {
            targetAddr = data.getStringExtra("addr");
            tv1.setText(targetAddr);
            Toast.makeText(MainActivity.this, data.getStringExtra("addr"), Toast.LENGTH_LONG).show();
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

    public void BTScan()
    {
        if (BTadapter == null)
        {
            Toast.makeText(MainActivity.this, "No Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!BTadapter.isEnabled())
        {
            BTadapter.enable();
        }

        scanner.startScan(callBack);
        hander.postDelayed(runnable, 10000);

    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            scanner.stopScan(callBack);
            Log.d("BLE1", "Scan stop!!");

        }
    };
    class MyScanCallBack extends ScanCallback
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (targetAddr.equals(result.getDevice().getAddress()))
            {
                BTDevice = result.getDevice();
                Log.d("BLE1", "Connect!!!") ;
            }

        }


    }
    public void clickConnect(View v)
    {
        BTScan();
    }

}
