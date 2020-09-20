package com.example.msc.ui.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.example.msc.R;

/**
 * Activity used for the About page feature.
 */
public class AboutActivity extends Activity {

    /**
     * Part of Activity's Lifecycle. It sets the UI from the XML and the onClickListener events for
     * the "see docs" and "see demo" buttons.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // see documentation button
        final Button docsButton = findViewById(R.id.see_docs);
        docsButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pybullet.org/wordpress/"));
            startActivity(browserIntent);
        });

        // demo video button
        final Button demoButton = findViewById(R.id.see_demo);
        demoButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/T9KT7nXX3bU"));
            startActivity(browserIntent);
        });
    }
}