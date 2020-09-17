package com.example.msc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {

    private static final int DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new Handler().postDelayed(() -> {
            final Intent intent = new Intent(LogoActivity.this, MainActivity.class);
            startActivity(intent);
        }, DELAY_MS);
    }
}
