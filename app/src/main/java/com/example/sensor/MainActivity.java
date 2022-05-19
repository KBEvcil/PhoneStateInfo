package com.example.sensor;

import java.lang.Math;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mySensorManager;
    TextView distanceText, accelerationText, stateText;
    Button btnSendBC;
    String states[] = {"Cepte ve Kullanici Hareketli", "Cepte ve Kullanici Hareketsiz", "Masada"};
    SensorManager sensorManager;
    Sensor sensor, sensor2;

    float distance = 5f;
    double maxAcceleration = 0;
    int phoneState = 0;

    AirplaneModeChangeReceiver airplaneModeChangedReceiver = new AirplaneModeChangeReceiver();

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(distance < 2.5f) {
                if(phoneState != 0 && maxAcceleration > 15f) {
                    phoneState = 0;
                    broadcastState(phoneState);
                }else if(phoneState != 1 && maxAcceleration < 15f) {
                    phoneState = 1;
                    broadcastState(phoneState);
                }
            } else if(phoneState != 2){
                phoneState = 2;
                broadcastState(phoneState);
            }
            stateText.setText("State: " + states[phoneState]);
            maxAcceleration = 0;
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        distanceText = findViewById(R.id.distance_text);
        accelerationText = findViewById(R.id.accel_text);
        stateText = findViewById(R.id.state_text);





    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangedReceiver, filter);

        runnable.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangedReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensor2, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = sensorEvent.values[0];
            this.distance = distance;
            distanceText.setText("Distance: " + this.distance);

        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double acceleration = 0;
            for(int i=0;i<3;i++) {
                acceleration += sensorEvent.values[i] * sensorEvent.values[i];
            }
            acceleration = Math.sqrt(acceleration);
            if(acceleration > maxAcceleration)
                maxAcceleration = acceleration;
            accelerationText.setText("Acceleration: " + (float)this.maxAcceleration);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void broadcastState(int phoneState) {
        Intent intent = new Intent();
        intent.setAction("com.example.sensor.sendBroadcast");
        intent.putExtra("state", phoneState);
        intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }

}