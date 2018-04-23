package com.example.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
    private boolean acc;
    private boolean gyro;
    private JSONObject accJSON;
    private JSONObject gyroJSON;
    private int ACCELEROMETER;
    private int GYROSCOPE;
    private int message_count;

    public Sensors (Context context) {
        this.context = context;
        checkSensors();
        registerSensors();
        Log.i(TAG, "Sensors Have Been Registered");
        acc = false;
        gyro = false;
        ACCELEROMETER = 0;
        GYROSCOPE = 1;
        message_count = 0;
    }

    public void startConnection(BluetoothSocket socket) {
        c = new ConnectedThread(socket);
    }

    public void run() {
        while (true) {
            try {
            } catch (Exception e) {
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
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensors() {
        mSensorManager.unregisterListener(this, mAcc);
        mSensorManager.unregisterListener(this, mGyro);
        Log.i(TAG, "Sensors unregistered");
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
//                Log.i("Accelerormeter =", "x axis = "+ event.values[0]+ "y axis = "+ event.values[1]+ "z axis = "+ event.values[2]);
                try {
                    JSONObject msg = new JSONObject("{\"accelerometer\":{ \"xAxis\":"+ event.values[0] + "," +
                            "\"yAxis\":"+  event.values[1] + "," + "\"zAxis\":"+ event.values[2] + "} }");
                    syncSensors(ACCELEROMETER, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                try {
//                    Log.i("Gyro =", "x axis = "+ event.values[0]+ "y axis = "+ event.values[1]+ "z axis = "+ event.values[2]);
                    JSONObject msg = new JSONObject("{\"gyro\":{ \"xAxis\":"+ event.values[0] + "," +
                            "\"yAxis\":"+  event.values[1] + "," + "\"zAxis\":"+ event.values[2] + "} }");
                    syncSensors(GYROSCOPE, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

    }
/*
* acc = 0
* gyro = 1
* magneto = 2
* */
    public synchronized void syncSensors(int sender, JSONObject msg) {

        switch (sender) {
            case 0:
                acc = true;
                accJSON = msg;
                break;
            case 1:
                gyro = true;
                gyroJSON = msg;
                break;
        }
        if(acc && gyro) {
            message_count++;
            try {
                JSONObject combined = new JSONObject();
                combined.put("accelerometer", accJSON.get("accelerometer"));
                combined.put("gyro", gyroJSON.get("gyro"));
//                Log.i(TAG, combined.toString());
                c.write(combined.toString().getBytes());
                acc = false;
                gyro = false;
            } catch (IOException e) {
                Log.i(TAG, String.valueOf(message_count));
                e.printStackTrace();
                unregisterSensors();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



}
