package com.example.msc.ui.inversekinematics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.R;
import com.example.msc.api.ZeroMQMessageAsyncTask;
import com.example.msc.api.models.InverseKinematicsModel;
import com.example.msc.api.models.RequestType;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Activity used for the Inverse Kinematics feature
 */
public class InverseKinematicsActivity extends AppCompatActivity {

    //class fields for every coordinate(xCoord,yCoord,zCoord) and orientation(aOrient, bOrient, cOrient)
    private TextInputEditText xCoord;
    private TextInputEditText yCoord;
    private TextInputEditText zCoord;
    private TextInputEditText aOrient;
    private TextInputEditText bOrient;
    private TextInputEditText cOrient;
    private Gson gson;

    /**
     * Part of Activity's Lifecycle. It sets the UI from the XML
     * and sets onClickListener for the submit button that the user presses
     * to submit the inverse kinematics.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inverse_kinematics);
        // submit button for sending the inverse kinematics to the simulation
        final Button button = findViewById(R.id.submit);
        button.setOnClickListener(v -> {
            try {
                new ZeroMQMessageAsyncTask().execute(getTaskInput());
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_SHORT).show();
            }
        });

        xCoord = findViewById(R.id.acoord_text);
        yCoord = findViewById(R.id.ycoord_text);
        zCoord = findViewById(R.id.zcoord_text);
        aOrient = findViewById(R.id.alpha_text);
        bOrient = findViewById(R.id.beta_text);
        cOrient = findViewById(R.id.theta_text);

        // information pop-up button
        final ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage(getString(R.string.inverse_kinematics_info_message))
                .setPositiveButton(R.string.ok_got_it, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.need_more_help, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/Inverse_kinematics"));
                    startActivity(browserIntent);
                })
                .show()
        );

        gson = new GsonBuilder().create();

    }

    /**
     * Assembles the inverse kinematics info into {@link InverseKinematicsModel} and
     * then converts it to a JSON string
     *
     * @return the inverse kinematics model as a json
     */
    protected String getTaskInput() {
        final InverseKinematicsModel model = new InverseKinematicsModel(
                RequestType.INVERSE_KINEMATICS,
                Double.parseDouble(xCoord.getText().toString()),
                Double.parseDouble(yCoord.getText().toString()),
                Double.parseDouble(zCoord.getText().toString()),
                Double.parseDouble(aOrient.getText().toString()),
                Double.parseDouble(bOrient.getText().toString()),
                Double.parseDouble(cOrient.getText().toString())
        );

        return gson.toJson(model);
    }
}