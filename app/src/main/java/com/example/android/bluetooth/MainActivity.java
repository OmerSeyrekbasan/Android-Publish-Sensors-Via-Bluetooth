package com.example.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private String TAG = "Main Thread";
    private final int REQUEST_ENABLE_BT = 10;
    private final int REQUEST_ENABLE_DC = 95;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBluetooth();
        enableBluetooth();
//        mBluetoothAdapter.startDiscovery();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter);

//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.i(deviceName, deviceHardwareAddress);
//            }
//        }
    }


    private void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("ERROR","Device doesn't support bluetooth!");
        }
    }

    private void enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "control thread will be started!" );
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "start thread will be started!" );
            startActivityForResult( new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_ENABLE_DC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                startActivityForResult( new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_ENABLE_DC);
                break;
            case REQUEST_ENABLE_DC:
                Log.d(TAG, "accept thread will be started!" );
                AcceptThread a = new AcceptThread(mBluetoothAdapter, this);
                a.run();
                break;
        }
//
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
//            startActivityForResult( new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), REQUEST_ENABLE_DC);
//        } else if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED ) {
//            finish();
//            return;
//        } else if(requestCode == REQUEST_ENABLE_DC && resultCode == RESULT_OK) {
//            Log.d(TAG, "accept thread will be started!" );
//            AcceptThread a = new AcceptThread(mBluetoothAdapter, this);
//            a.start();
//        } else if(requestCode == REQUEST_ENABLE_DC && resultCode == RESULT_CANCELED) {
//            finish();
//            return;
//        }
    }


}


