package com.example.msc.motion;

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

    private final MutableLiveData<String> xLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> yLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> zLiveData = new MutableLiveData<>();

    private final MutableLiveData<InverseKinematicsModel> inverseKinematicsModelLiveData = new MutableLiveData<>();

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        private int count = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            final double x = (((event.values[0] + Math.PI) - Math.PI / 2) % (2 * Math.PI)) - Math.PI;
            final double y = event.values[1];
            final double z = event.values[2];

            xLiveData.setValue(String.format(Locale.UK, "%.6f", x));
            yLiveData.setValue(String.format(Locale.UK, "%.6f", y));
            zLiveData.setValue(String.format(Locale.UK, "%.6f", z));

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

    public LiveData<String> getyLiveData() {
        return yLiveData;
    }

    public LiveData<String> getzLiveData() {
        return zLiveData;
    }

    public LiveData<InverseKinematicsModel> getInverseKinematicsModelLiveData() {
        return inverseKinematicsModelLiveData;
    }
}
