package com.example.teacher.db052001;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
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

import java.util.List;
import java.util.UUID;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    public UUID UUID_IRT_SERV = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    public UUID UUID_IRT_DATA = UUID.fromString("00002a1e-0000-1000-8000-00805f9b34fb");
    public UUID CLIENT_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //定義手機的UUID
    final int REQUEST_CODE = 123;
    String targetAddr;
    BluetoothAdapter BTadapter;
    BluetoothLeScanner scanner;
    BluetoothDevice BTDevice;
    BluetoothGatt BTGatt;
    MyScanCallBack callBack = new MyScanCallBack();
    Handler hander;
    TextView tv1;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hander = new Handler();
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BTadapter = manager.getAdapter();
        scanner = BTadapter.getBluetoothLeScanner();
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

    public BluetoothGattCallback GattCallback = new BluetoothGattCallback() {
        public void SetupSensorStep(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;

            for (BluetoothGattCharacteristic c : gatt.getService(UUID_IRT_SERV).getCharacteristics())
            {

                for (BluetoothGattDescriptor desc : c.getDescriptors())
                {

                }
            }

            // Enable local notifications
            characteristic = gatt.getService(UUID_IRT_SERV).getCharacteristic(UUID_IRT_DATA);
            gatt.setCharacteristicNotification(characteristic, true);
            // Enabled remote notifications
            descriptor = characteristic.getDescriptor(CLIENT_CONFIG_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);

        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE1", "Connected to GATT Server");
                gatt.discoverServices();
            } else {
                Log.d("BLE1", "Disconnected from GATT Server");
                gatt.disconnect();
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d("BLE1", "New Service!!");
            List<BluetoothGattService> list = gatt.getServices();
            for (BluetoothGattService s : list)
            {
                Log.d("BLE1", "In Service:" + s.getUuid());
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (UUID_IRT_DATA.equals(characteristic.getUuid())) {

                byte[] b = new byte[characteristic.getValue().length];
                b = characteristic.getValue();
                Byte b1 = b[2];
                Byte b2 = b[1];

                final int t = (b1 & 0xFF) * 255 + (b2 & 0xFF);

                Log.d("BLE1", gatt.getDevice().getAddress() + ":DATA:" + bytesToHex(b) + ",Temp:" + t);

            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    class MyScanCallBack extends ScanCallback
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d("BLE1", "Got: " + result.getDevice().getAddress()) ;
            if (targetAddr.equals(result.getDevice().getAddress()))
            {
                BTDevice = result.getDevice();
                BTGatt = BTDevice.connectGatt(getApplicationContext(), false, GattCallback); // 連接GATT
                Log.d("BLE1", "Connect!!!") ;
            }


        }


    }
    public void clickConnect(View v)
    {
        BTScan();
    }

}
