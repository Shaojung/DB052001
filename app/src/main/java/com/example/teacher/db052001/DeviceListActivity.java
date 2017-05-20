package com.example.teacher.db052001;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListActivity extends AppCompatActivity {
    BluetoothAdapter BTadapter;
    BluetoothLeScanner scanner;
    ArrayList<String> showStr = new ArrayList<>();
    ArrayList<String> addr = new ArrayList<>();
    ArrayAdapter adapter;
    MyScanCallBack callBack = new MyScanCallBack();
    Handler hander;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        hander = new Handler();
        lv = (ListView) findViewById(R.id.listView);
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BTadapter = manager.getAdapter();
        scanner = BTadapter.getBluetoothLeScanner();
        adapter = new ArrayAdapter(DeviceListActivity.this, android.R.layout.simple_list_item_1, showStr);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                it.putExtra("addr", addr.get(position));
                setResult(RESULT_OK, it);
                finish();
            }
        });
        BTScan();
    }

    public void BTScan()
    {
        if (BTadapter == null)
        {
            Toast.makeText(DeviceListActivity.this, "No Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!BTadapter.isEnabled())
        {
            BTadapter.enable();
        }
        addr.clear();
        showStr.clear();
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
            if (! addr.contains(result.getDevice().getAddress()))
            {
                addr.add(result.getDevice().getAddress());
                showStr.add(result.getDevice().getName() + "," + result.getDevice().getAddress());
                adapter.notifyDataSetChanged();
                Log.d("BLE1", result.getDevice().getAddress() + "," + result.getDevice().getName()) ;
            }

        }


    }
}
