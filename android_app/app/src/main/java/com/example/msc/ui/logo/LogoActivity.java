package com.example.msc.ui.logo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.ui.mainmenu.MainMenuActivity;
import com.example.msc.R;

/**
 * Activity used for the Logo screen feature.
 */
public class LogoActivity extends AppCompatActivity {

    // after the app is loaded, delay going to the main menu by 2 sec.
    private static final int DELAY_MS = 2000;

    /**
     * Part of Activity's Lifecycle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // delay going to main menu screen by 2 sec.
        new Handler().postDelayed(() -> {
            final Intent intent = new Intent(LogoActivity.this, MainMenuActivity.class);
            startActivity(intent);
        }, DELAY_MS);
    }
}
