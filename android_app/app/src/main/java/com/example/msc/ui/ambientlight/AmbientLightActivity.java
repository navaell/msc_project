package com.example.msc.ui.ambientlight;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.R;
import com.suke.widget.SwitchButton;

import java.util.Locale;

/**
 * Activity used for the ambient light feature.
 */
public class AmbientLightActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mLight;
    private TextView textView;

    private boolean isToggleEnabled = false;

    /**
     * Part of Activity's Lifecycle.
     */
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambient_light);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textView = (TextView)findViewById(R.id.sensor_data);

        // switch button - sets toggle listener
        final SwitchButton switchButton = findViewById(R.id.toggle);
        switchButton.setOnCheckedChangeListener((view, isChecked) -> {
            isToggleEnabled = isChecked;

            textView.setText("N/A");
        });

        // information button - sets click listener to open popup
        final ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage(R.string.light_sensor_info_message)
                .setPositiveButton(R.string.ok_got_it, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.need_more_help, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Lux"));
                    startActivity(browserIntent);
                })
                .show()
        );
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * On a sensor change and if the toggle button is enabled, display the value of lux up to 2 d.p
     */
    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (isToggleEnabled) {
            // The light sensor returns a single value.
            // Many sensors return 3 values, one for each axis.
            float lux = event.values[0];
            // Do something with this sensor value.

            textView.setText(String.format(Locale.UK, "%.2f lux", lux));
        }
    }
    /**
     * Part of Activity's Lifecycle. It subscribes this activity to the sensorManager for periodic
     *  light sensor updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Part of Activity's Lifecycle. It unregisters the listener receiving sensor updates.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
