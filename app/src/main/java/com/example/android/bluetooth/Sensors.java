package com.example.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class Sensors extends Thread implements SensorEventListener {
    private final String TAG = "Sensor";
    private ConnectedThread c;
    private Context context;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private Sensor mGyro;
    private Sensor mMagnet;

    public Sensors (Context context) {
        this.context = context;
        checkSensors();
        registerSensors();

    }

    public void startConnection(BluetoothSocket socket) {
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

    public void checkSensors() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.i(TAG, deviceSensors.toString());

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
            Log.e(TAG, "Accelerometer sensor does not exist");
        else mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null)
            Log.e(TAG, "GYROSCOPE sensor does not exist");
        else mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null)
            Log.e(TAG, "Linear Acceleration sensor does not exist");
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)
            Log.e(TAG, "Magnetometer sensor does not exist");
        else mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registerSensors() {
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensors() {
        //TODO unregister sensors on onpause()
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                Log.i("Accelerormeter =", "x axis = "+ event.values[0]+ "y axis = "+ event.values[1]+ "z axis = "+ event.values[2]);
        }

    }




}
