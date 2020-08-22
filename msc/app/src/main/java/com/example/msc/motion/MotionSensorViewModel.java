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
    private boolean toggleEnabled = false;

    private final MutableLiveData<String> xLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> yLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> zLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> combinedColour = new MutableLiveData<>();

    private final MutableLiveData<InverseKinematicsModel> inverseKinematicsModelLiveData = new MutableLiveData<>();

    private int radianToHex2PIRange(double x){
        double range = 2*Math.PI;
        double unit = range/255;
        int rgbNubmer = (int) (x/unit);
        return rgbNubmer;
    }

    private int radianToHexPIRange(double x){
        double range = Math.PI;
        double unit = range/255;
        int rgbNubmer = (int) (x/unit);
        return rgbNubmer;
    }

    private int combinedColourFromRadianValues (double x, double y, double z){
        int xRGB = radianToHexPIRange(x);
        int yRGB = radianToHex2PIRange(y);
        int zRGB = radianToHex2PIRange(z);
        return Color.argb(255, xRGB, yRGB, zRGB);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        private int count = 0;


        @Override
        public void onSensorChanged(SensorEvent event) {
            if (toggleEnabled) {
                final double x = (((event.values[0] + Math.PI) +  (3 * Math.PI / 2)) % (2 * Math.PI)) - Math.PI;
                final double y = event.values[1];
                final double z = event.values[2];

                combinedColour.setValue(combinedColourFromRadianValues(x,y,z));

                xLiveData.setValue(String.format(Locale.UK, "%.3f", x));
                yLiveData.setValue(String.format(Locale.UK, "%.3f", y));
                zLiveData.setValue(String.format(Locale.UK, "%.3f", z));

                if (count % 10 == 0) {
                    final InverseKinematicsModel newModel = new InverseKinematicsModel(
                            RequestType.INVERSE_KINEMATICS,
                            0.5,
                            0.5,
                            0.5,
                            x,
                            z,
                            y);

                    inverseKinematicsModelLiveData.setValue(newModel);
                }

                count++;
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
        }
    }
}
