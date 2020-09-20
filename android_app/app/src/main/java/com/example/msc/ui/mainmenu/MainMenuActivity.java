package com.example.msc.ui.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.R;
import com.example.msc.ui.about.AboutActivity;
import com.example.msc.ui.ambientlight.AmbientLightActivity;
import com.example.msc.ui.inversekinematics.InverseKinematicsActivity;
import com.example.msc.ui.motion.MotionControlActivity;

/**
 * Activity used for the Main Menu feature.
 */
public class MainMenuActivity extends AppCompatActivity {

    /**
     * Part of Activity's Lifecycle. It sets the UI from the XML.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // inverse kinematics button
        Button buttonInverseKinematics = findViewById(R.id.inverse_kinematics);
        buttonInverseKinematics.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), InverseKinematicsActivity.class);
            startActivity(intent);
        });

        // about button
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AboutActivity.class);
            startActivity(intent);
        });

        // ambient light button
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(vi -> {
            Intent intent = new Intent(vi.getContext(), AmbientLightActivity.class);
            startActivity(intent);
        });

        // motion control button
        Button sensorButton = findViewById(R.id.motion_sensor_button);
        sensorButton.setOnClickListener(vie -> {
            Intent intent = new Intent(vie.getContext(), MotionControlActivity.class);
            startActivity(intent);
        });
    }

    /**
     * When the user presses back button on the main menu, exit the app
     */
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}