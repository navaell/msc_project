package com.example.msc.motion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.msc.MessageListenerHandler;
import com.example.msc.R;
import com.example.msc.ZeroMQMessageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suke.widget.SwitchButton;

public class MotionSensorActivity extends AppCompatActivity {

    private SensorManager sensorManager;

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

        final TextView textView1 = findViewById(R.id.motion_sensor_data1);
        final TextView textView2 = findViewById(R.id.motion_sensor_data2);
        final TextView textView3 = findViewById(R.id.motion_sensor_data3);

        viewModel.getxLiveData().observe(this, newValue -> textView1.setText(newValue));
        viewModel.getyLiveData().observe(this, newValue -> textView3.setText(newValue));
        viewModel.getzLiveData().observe(this, newValue -> textView2.setText(newValue));
        viewModel.getCombinedColour().observe(this, newColour -> {
            textView1.setTextColor(newColour);
            textView2.setTextColor(newColour);
            textView3.setTextColor(newColour);
        });

        viewModel.getInverseKinematicsModelLiveData().observe(
                this,
                newData -> new ZeroMQMessageTask(clientMessageHandler).execute(gson.toJson(newData))
        );

        final SwitchButton switchButton = findViewById(R.id.toggle);
        switchButton.setOnCheckedChangeListener((view, isChecked) -> viewModel.onButtonToggled(isChecked));

        final ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Info Message Content")
                .setPositiveButton("Dismiss", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Sensor orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(viewModel.getSensorEventListener(), orientation,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(viewModel.getSensorEventListener());
    }
}