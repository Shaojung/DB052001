package com.example.teacher.db052001;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DeviceListActivity extends AppCompatActivity {
    BluetoothAdapter adapter;
    BluetoothLeScanner scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = manager.getAdapter();
        scanner = adapter.getBluetoothLeScanner();
        BTScan();
    }

    public void BTScan()
    {
        if (adapter == null)
        {
            Toast.makeText(DeviceListActivity.this, "No Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!adapter.isEnabled())
        {
            adapter.enable();
        }
        scanner.startScan(new MyScanCallBack());
    }
    class MyScanCallBack extends ScanCallback
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d("BLE", result.getDevice().getAddress());
        }
    }
}
