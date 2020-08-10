package com.example.msc.motion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.msc.MessageListenerHandler;
import com.example.msc.R;
import com.example.msc.ZeroMQMessageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MotionSensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;

    private final Gson gson = new GsonBuilder().create();

    private MotionSensorViewModel viewModel;

    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(messageBody -> {
    }, MessageListenerHandler.PAYLOAD_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_sensor);

        viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(this.getApplication())
                .create(MotionSensorViewModel.class);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        textView1 = findViewById(R.id.motion_sensor_data1);
        textView2 = findViewById(R.id.motion_sensor_data2);
        textView3 = findViewById(R.id.motion_sensor_data3);

        viewModel.getxLiveData().observe(this, newValue -> textView1.setText(newValue));
        viewModel.getyLiveData().observe(this, newValue -> textView3.setText(newValue));
        viewModel.getzLiveData().observe(this, newValue -> textView2.setText(newValue));

        viewModel.getInverseKinematicsModelLiveData().observe(
                this,
                newData -> new ZeroMQMessageTask(clientMessageHandler).execute(gson.toJson(newData))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(viewModel.getSensorEventListener(), rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(viewModel.getSensorEventListener());
    }
}