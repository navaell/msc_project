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

public class MotionSensorViewModel extends ViewModel {
    private static final double ACCELEROMETER_MULTIPLIER = 1;
    private boolean toggleEnabled = false;

    private final MutableLiveData<String> xLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> yLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> zLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> combinedColour = new MutableLiveData<>();

    private final MutableLiveData<InverseKinematicsModel> inverseKinematicsModelLiveData = new MutableLiveData<>();

    private int radianToHex2PIRange(double x) {
        double range = 2 * Math.PI;
        double unit = range / 255;
        int rgbNubmer = (int) ((x + Math.PI) / unit);
        return rgbNubmer;
    }

    private int radianToHexPIRange(double x) {
        double range = Math.PI;
        double unit = range / 255;
        int rgbNubmer = (int) ((x + (Math.PI / 2)) / unit);
        return rgbNubmer;
    }

    private int combinedColourFromRadianValues(double x, double y, double z) {
        int xRGB = radianToHex2PIRange(x);
        int yRGB = radianToHex2PIRange(y);
        int zRGB = radianToHexPIRange(z);
        return Color.argb(255, xRGB, yRGB, zRGB);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        private int count = 0;
        private final float[] position = new float[3];
        private final float[] orientationReading = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                System.arraycopy(event.values, 0, orientationReading, 0, orientationReading.length);
            }  else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                final double offsetX = event.values[0];
                final double offsetY = event.values[1];
                final double offsetZ = event.values[2];

                position[0] += offsetX * ACCELEROMETER_MULTIPLIER;
                position[1] += offsetY * ACCELEROMETER_MULTIPLIER;
                position[2] += offsetZ * ACCELEROMETER_MULTIPLIER;

                for (int i = 0; i < 3; i++) {
                    if (position[i] > 1) {
                        position[i] = 1;
                    } else if (position[i] < 0) {
                        position[i] = 0;
                    }
                }
            }

            if (toggleEnabled && count % 2 == 0) {
                final double x = orientationReading[0];//(((event.values[0] + Math.PI) +  (3 * Math.PI / 2)) % (2 * Math.PI)) - Math.PI;
                final double y = orientationReading[1];
                final double z = 0 - orientationReading[2];

                //todo combinedColour.setValue(combinedColourFromRadianValues(x, y, z));

                xLiveData.setValue(String.format(Locale.UK, "%.3f", position[0]));
                yLiveData.setValue(String.format(Locale.UK, "%.3f", position[1]));
                zLiveData.setValue(String.format(Locale.UK, "%.3f", position[2]));

                final InverseKinematicsModel newModel = new InverseKinematicsModel(
                        RequestType.INVERSE_KINEMATICS,
                        position[0],
                        position[1],
                        position[2],
                        x,
                        z,
                        y);

                inverseKinematicsModelLiveData.setValue(newModel);
            }

            count++;
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
        }
    }
}
