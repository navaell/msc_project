package com.example.msc.ui.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.msc.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        final Button docsButton = findViewById(R.id.see_docs);
        docsButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pybullet.org/wordpress/"));
            startActivity(browserIntent);
        });


        final Button demoButton = findViewById(R.id.see_demo);
        demoButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=SbYos2z8pjI"));
            startActivity(browserIntent);
        });






    }
}