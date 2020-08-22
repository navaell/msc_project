package com.example.msc;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msc.models.InverseKinematicsModel;
import com.example.msc.models.RequestType;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InverseKinematicsActivity extends AppCompatActivity {

    private TextInputEditText xCoord;
    private TextInputEditText yCoord;
    private TextInputEditText zCoord;
    private TextInputEditText aOrient;
    private TextInputEditText bOrient;
    private TextInputEditText cOrient;
    private Gson gson;

    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(
            messageBody -> {
            },
            MessageListenerHandler.PAYLOAD_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inverse_kinematics);
        final Button button = findViewById(R.id.submit);
        button.setOnClickListener(v -> new ZeroMQMessageTask(clientMessageHandler).execute(getTaskInput()));

        xCoord = findViewById(R.id.acoord_text);
        yCoord = findViewById(R.id.ycoord_text);
        zCoord = findViewById(R.id.zcoord_text);
        aOrient = findViewById(R.id.alpha_text);
        bOrient = findViewById(R.id.beta_text);
        cOrient = findViewById(R.id.theta_text);

        final ImageView infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Info Message Content")
                .setPositiveButton("Dismiss", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show()
        );

        gson = new GsonBuilder().create();

    }

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