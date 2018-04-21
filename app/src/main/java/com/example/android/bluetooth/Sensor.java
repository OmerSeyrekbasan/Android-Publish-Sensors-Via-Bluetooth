package com.example.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public class Sensor extends Thread{
    private final String TAG = "Sensor";
    private ConnectedThread c;

    public Sensor (BluetoothSocket socket) {
        c = new ConnectedThread(socket);
    }

    public void run() {
        while (true) {
            try {
                c.write("hello".getBytes());
            } catch (IOException e) {
                break;
            }
        }

    }


}
