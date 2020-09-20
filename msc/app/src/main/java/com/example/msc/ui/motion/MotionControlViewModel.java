package com.example.msc.ui.motion;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.msc.api.models.InverseKinematicsModel;
import com.example.msc.api.models.RequestType;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class containing all the logic for the motion control feature. This updates the UI using the
 * observer pattern with LiveData. Also, this holds no reference to the activity class, avoiding
 * possible memory leaks.
 */
public class MotionControlViewModel extends ViewModel {
    private boolean toggleEnabled = false;

    // LiveData Observers
    private final MutableLiveData<String> alphaLiveData = new MutableLiveData<>("N/A");
    private final MutableLiveData<String> gammaLiveData = new MutableLiveData<>("N/A");
    private final MutableLiveData<String> betaLiveData = new MutableLiveData<>("N/A");
    private final MutableLiveData<Integer> combinedColour = new MutableLiveData<>();
    private final MutableLiveData<InverseKinematicsModel> zeroMqModelLiveData = new MutableLiveData<>();

    private final AtomicBoolean readyToSend = new AtomicBoolean(true);
    private final Runnable onMessageSent = () -> readyToSend.set(true);

    /**
     * Method used to map any value from its range [minValue, maxValue] to the [0, 255] range,
     * used by the RGB standard.
     *
     * @param value    value to be mapped
     * @param minValue the lower boundary of the range for the given value
     * @param maxValue the upper boundary of the range for the given value
     * @return a value in the range 0 <= return <= 255
     */
    private int mapValueToColour(double value, double minValue, double maxValue) {
        // offset into positive range
        final double newValue = value + Math.abs(minValue);
        final double newMaxValue = Math.abs(minValue) + maxValue;

        return (int) ((newValue / newMaxValue) * 255);
    }

    /**
     * Method used to map three angles to a single RGB colour
     *
     * @param alpha angle mapping to R. In the range -180 to 180 deg
     * @param gamma angle mapping to G. In the range 0 to 360 deg
     * @param beta  angle mapping to B. In the range -90 to 90 deg
     * @return int representing a colour mapped from the RGB equivalent of the angle readings
     */
    private int combinedColourFromRadianValues(double alpha, double gamma, double beta) {
        int xRGB = mapValueToColour(alpha, -180, 180);
        int yRGB = mapValueToColour(gamma, 0, 360);
        int zRGB = mapValueToColour(beta, -90, 90);
        return Color.argb(255, xRGB, yRGB, zRGB);
    }

    /**
     * Anonymous class used for intercepting sensor events. This is subscribed to the
     * {@link android.hardware.SensorManager} in the {@link MotionControlActivity}
     */
    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        /**
         * Method invoked by the {@link android.hardware.SensorManager} when there's new sensor
         *  data to be processed.
         *
         * If the toggle is enabled and ZeroMQ is ready to send more data to PyBullet, the sensor
         *  values are read from the object passed as argument. The raw readings are then processed
         *  and the new values for text and colour are being passed to the LiveData observers. Also,
         *  a new model is being created and passed to the zero mq LiveData. If the toggle is disabled
         *  or ZeroMQ is not ready, the sensor readings are ignored.
         *
         * Note: ZeroMQ is considered ready when the last message has been successfully received
         *  by PyBullet.
         *
         * @param event object containing the new sensor readings
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (toggleEnabled && readyToSend.getAndSet(false)) {
                final double rawAlpha = event.values[1];
                final double alpha;
                if (rawAlpha > 0) { // offset alpha sensor reading to match PyBullet's point of reference
                    alpha = 180 - rawAlpha;
                } else {
                    alpha = -180 - rawAlpha;
                }

                final double gamma = 360 - event.values[0]; // offset gamma sensor reading to match PyBullet's point of reference
                final double beta = event.values[2];

                // publish new colour for text fields
                combinedColour.setValue(combinedColourFromRadianValues(alpha, gamma, beta));

                // publish new sensor readings for the text field
                alphaLiveData.setValue(String.format(Locale.UK, "%.3f°", alpha));
                gammaLiveData.setValue(String.format(Locale.UK, "%.3f°", gamma));
                betaLiveData.setValue(String.format(Locale.UK, "%.3f°", beta));

                final InverseKinematicsModel newModel = new InverseKinematicsModel(
                        RequestType.INVERSE_KINEMATICS,
                        0.5,
                        0.5,
                        0.5,
                        (alpha * Math.PI) / 180,
                        0,//(beta * Math.PI) / 180,
                        (gamma * Math.PI) / 180);

                // publish new model to be sent via ZeroMQ
                zeroMqModelLiveData.setValue(newModel);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * Method called from the {@link MotionControlActivity} when the switch is toggled on or off.
     * <p>
     * It is used to start/stop the sensor readings from being processed and to reset the text field
     * values when needed.
     *
     * @param enabled true when switch is on ; false otherwise
     */
    public void onButtonToggled(final boolean enabled) {
        toggleEnabled = enabled;

        if (!enabled) {
            alphaLiveData.setValue("N/A");
            gammaLiveData.setValue("N/A");
            betaLiveData.setValue("N/A");
            combinedColour.setValue(Color.BLACK);
        }
    }

    public SensorEventListener getSensorEventListener() {
        return sensorEventListener;
    }

    public LiveData<String> getAlphaLiveData() {
        return alphaLiveData;
    }

    public LiveData<Integer> getCombinedColour() {
        return combinedColour;
    }

    public LiveData<String> getGammaLiveData() {
        return gammaLiveData;
    }

    public LiveData<String> getBetaLiveData() {
        return betaLiveData;
    }

    public LiveData<InverseKinematicsModel> getZeroMqModelLiveData() {
        return zeroMqModelLiveData;
    }

    public Runnable getOnMessageSent() {
        return onMessageSent;
    }
}
