package com.example.msc;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.models.InverseKinematicsModel;
import com.example.msc.models.RequestType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

public class MotionSensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Gson gson;

    int count = 0;

    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(
            messageBody -> {
            },
            MessageListenerHandler.PAYLOAD_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        textView1 = (TextView) findViewById(R.id.motion_sensor_data1);
        textView2 = (TextView) findViewById(R.id.motion_sensor_data2);
        textView3 = (TextView) findViewById(R.id.motion_sensor_data3);

        gson = new GsonBuilder().create();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final double x = (((event.values[0] + Math.PI) - Math.PI / 2) % (2 * Math.PI)) - Math.PI;
        final double y = event.values[1];
        final double z = event.values[2];

        textView1.setText(String.format(Locale.UK, "%.6f", x));
        textView2.setText(String.format(Locale.UK, "%.6f", z));
        textView3.setText(String.format(Locale.UK, "%.6f", y));

        if (count % 10 == 0) {
            new ZeroMQMessageTask(clientMessageHandler).execute(getTaskInput());
        }

        count++;
        if (count < 0) {
            count = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private String getTaskInput() {
        final InverseKinematicsModel model = new InverseKinematicsModel(RequestType.INVERSE_KINEMATICS,
                0.5,
                0.5,
                0.5,
                Double.parseDouble(textView1.getText().toString()),
                Double.parseDouble(textView2.getText().toString()),
                Double.parseDouble(textView3.getText().toString())
        );
        return gson.toJson(model);
    }
}