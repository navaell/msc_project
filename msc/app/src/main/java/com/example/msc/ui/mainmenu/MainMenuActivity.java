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

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button buttonInverseKinematics = findViewById(R.id.inverse_kinematics);
        buttonInverseKinematics.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), InverseKinematicsActivity.class);
            startActivity(intent);
        });

        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AboutActivity.class);
            startActivity(intent);
        });

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(vi -> {
            Intent intent = new Intent(vi.getContext(), AmbientLightActivity.class);
            startActivity(intent);
        });

        Button sensorButton = findViewById(R.id.motion_sensor_button);
        sensorButton.setOnClickListener(vie -> {
            Intent intent = new Intent(vie.getContext(), MotionControlActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}