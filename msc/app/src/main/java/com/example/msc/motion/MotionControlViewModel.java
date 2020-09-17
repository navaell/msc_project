package com.example.msc.motion;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.msc.models.InverseKinematicsModel;
import com.example.msc.models.RequestType;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MotionControlViewModel extends ViewModel {
    private boolean toggleEnabled = false;

    private final MutableLiveData<String> xLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> yLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> zLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> combinedColour = new MutableLiveData<>();

    private final MutableLiveData<InverseKinematicsModel> inverseKinematicsModelLiveData = new MutableLiveData<>();

    private final AtomicBoolean readyToSend = new AtomicBoolean(true);
    private final Runnable onMessageSent = () -> readyToSend.set(true);

    private int mapValueToColour(double value, double minValue, double maxValue) {
        // offset into positive range
        final double newValue = value + Math.abs(minValue);
        final double newMaxValue = Math.abs(minValue) + maxValue;

        return (int) ((newValue/newMaxValue) * 255);
    }

    private int combinedColourFromRadianValues(double alpha, double gamma, double beta) {
        int xRGB = mapValueToColour(alpha, -180, 180);
        int yRGB = mapValueToColour(gamma, 0, 360);
        int zRGB = mapValueToColour(beta, -90, 90);
        return Color.argb(255, xRGB, yRGB, zRGB);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (toggleEnabled && readyToSend.getAndSet(false)) {
                final double rawAlpha = event.values[1];
                final double alpha;
                if (rawAlpha > 0) {
                    alpha = 180 - rawAlpha;
                } else {
                    alpha = -180 - rawAlpha;
                }

                final double gamma = 360 - event.values[0];

                final double rawBeta = event.values[2];
                final double beta = rawBeta + 90;

                combinedColour.setValue(combinedColourFromRadianValues(alpha, gamma, beta));

                xLiveData.setValue(String.format(Locale.UK, "%.3f°", alpha));// * 180 / Math.PI));
                yLiveData.setValue(String.format(Locale.UK, "%.3f°", gamma));// * 180 / Math.PI));
                zLiveData.setValue(String.format(Locale.UK, "%.3f°", beta));// * 180 / Math.PI));

                final InverseKinematicsModel newModel = new InverseKinematicsModel(
                        RequestType.INVERSE_KINEMATICS,
                        0.5,
                        0.5,
                        0.5,
                        (alpha * Math.PI) / 180,
                        0,//(beta * Math.PI) / 180,
                        (gamma * Math.PI) / 180);

                inverseKinematicsModelLiveData.setValue(newModel);
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public SensorEventListener getSensorEventListener() {
        return sensorEventListener;
    }

    public LiveData<String> getxLiveData() {
        return xLiveData;
    }

    public LiveData<Integer> getCombinedColour() {
        return combinedColour;
    }

    public LiveData<String> getyLiveData() {
        return yLiveData;
    }

    public LiveData<String> getzLiveData() {
        return zLiveData;
    }

    public LiveData<InverseKinematicsModel> getInverseKinematicsModelLiveData() {
        return inverseKinematicsModelLiveData;
    }

    public void onButtonToggled(final boolean enabled) {
        toggleEnabled = enabled;

        if (!enabled) {
            xLiveData.setValue("N/A");
            yLiveData.setValue("N/A");
            zLiveData.setValue("N/A");
            combinedColour.setValue(Color.BLACK);
        }
    }

    public Runnable getOnMessageSent() {
        return onMessageSent;
    }
}
