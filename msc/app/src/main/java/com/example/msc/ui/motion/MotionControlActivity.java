package com.example.msc.ui.motion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.msc.R;
import com.example.msc.api.ZeroMQMessageAsyncTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.suke.widget.SwitchButton;

/**
 * Activity used for the motion control feature. It follows the CLEAN architecture by implementing
 *  the observer MVVM pattern to avoid circular referencing between the Activity and the ViewModel.
 *  Hence, all updates are published via the Livedata.
 */
public class MotionControlActivity extends AppCompatActivity {
    private final Gson gson = new GsonBuilder().create(); // used to convert objects to json

    private SensorManager sensorManager; // used to subscribe to sensor events
    private MotionControlViewModel viewModel; // contains logic driving ui

    /**
     * Part of Activity's Lifecycle. It sets the UI from the XML, creates the ViewModel containing
     * the logic controlling the UI and it subscribes to the LiveData Observers exposed from the
     * ViewModel.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_control);

        // Creates ViewModel or restores the previous on if there's a configuration change
        viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(this.getApplication())
                .create(MotionControlViewModel.class);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        final TextView textView1 = findViewById(R.id.motion_sensor_data1);
        final TextView textView2 = findViewById(R.id.motion_sensor_data2);
        final TextView textView3 = findViewById(R.id.motion_sensor_data3);

        // Note: LiveData is lifecycle aware and it automatically hangs/removes the subscription
        //  when the activity is paused/destroyed.
        viewModel.getAlphaLiveData().observe(this, newValue -> textView1.setText(newValue));
        viewModel.getGammaLiveData().observe(this, newValue -> textView2.setText(newValue));
        viewModel.getBetaLiveData().observe(this, newValue -> textView3.setText(newValue));
        viewModel.getCombinedColour().observe(this, newColour -> {
            textView1.setTextColor(newColour);
            textView2.setTextColor(newColour);
            textView3.setTextColor(newColour);
        });

        viewModel.getZeroMqModelLiveData().observe(
                this,
                newData -> new ZeroMQMessageAsyncTask(viewModel.getOnMessageSent()).execute(gson.toJson(newData))
        );

        final SwitchButton switchButton = findViewById(R.id.toggle);
        switchButton.setOnCheckedChangeListener((view, isChecked) -> viewModel.onButtonToggled(isChecked));

        final ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage(R.string.motion_sensor_info_message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show()
        );
    }

    /**
     * Part of Activity's Lifecycle. It subscribes the ViewModel to the sensor events for periodic
     * sensor reading updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        final Sensor orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(viewModel.getSensorEventListener(), orientation,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Part of Activity's Lifecycle. It unregisters the ViewModel from the sensor events.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(viewModel.getSensorEventListener());
    }
}