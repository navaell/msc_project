package com.example.msc.motion;

import android.hardware.SensorEvent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.msc.api.models.InverseKinematicsModel;
import com.example.msc.ui.motion.MotionControlViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MotionControlViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private List<String> alphaValues;
    private List<String> gammaValues;
    private List<String> betaValues;
    private List<InverseKinematicsModel> inverseKinematicsModels;

    private final float[] testValues = new float[]{1.1f, 1.2f, 1.3f};

    @Mock
    private SensorEvent sensorEvent;
    private MotionControlViewModel viewModel;

    @Before
    public void setUp() {
        alphaValues = new LinkedList<>();
        gammaValues = new LinkedList<>();
        betaValues = new LinkedList<>();
        inverseKinematicsModels = new LinkedList<>();

        viewModel = new MotionControlViewModel();

        viewModel.getAlphaLiveData().observeForever(value -> alphaValues.add(value));
        viewModel.getGammaLiveData().observeForever(value -> gammaValues.add(value));
        viewModel.getBetaLiveData().observeForever(value -> betaValues.add(value));
        viewModel.getZeroMqModelLiveData().observeForever(value -> inverseKinematicsModels.add(value));
    }

    @Test
    public void anglesDataIsNABeforeSensorChangedEvent() {
        Assert.assertEquals("N/A", alphaValues.get(0));
        Assert.assertEquals("N/A", gammaValues.get(0));
        Assert.assertEquals("N/A", betaValues.get(0));
    }

    @Test
    public void onSensorChangedDoesNotUpdateAnglesWhenToggleIsOff() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);
        Assert.assertEquals(1, alphaValues.size());
        Assert.assertEquals(1, gammaValues.size());
        Assert.assertEquals(1, betaValues.size());

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        // After sensor reading, we still have only 1 emission for "N/A"
        Assert.assertEquals(1, alphaValues.size());
        Assert.assertEquals(1, gammaValues.size());
        Assert.assertEquals(1, betaValues.size());
    }

    @Test
    public void onButtonToggledOffChangesAnglesToNA() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        viewModel.onButtonToggled(false);

        Assert.assertEquals(3, alphaValues.size());
        Assert.assertEquals(3, gammaValues.size());
        Assert.assertEquals(3, betaValues.size());

        Assert.assertEquals("N/A", alphaValues.get(2));
        Assert.assertEquals("N/A", gammaValues.get(2));
        Assert.assertEquals("N/A", betaValues.get(2));
    }

    @Test
    public void onSensorChangedUpdatesAlpha() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(2, alphaValues.size());
        Assert.assertEquals("178.800°", alphaValues.get(1));
    }

    @Test
    public void onSensorChangedUpdatesGamma() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(2, gammaValues.size());
        Assert.assertEquals("358.900°", gammaValues.get(1));
    }

    @Test
    public void onSensorChangedUpdatesBeta() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(2, betaValues.size());
        Assert.assertEquals("1.300°", betaValues.get(1));
    }

    @Test
    public void inverseKinematicsModelContainsExpectedData() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        Assert.assertEquals(1, inverseKinematicsModels.size());

        InverseKinematicsModel returned = inverseKinematicsModels.get(0);

        Assert.assertEquals(0.5, returned.getxCoordinate(), 0);
        Assert.assertEquals(0.5, returned.getyCoordinate(), 0);
        Assert.assertEquals(0.5, returned.getzCoordinate(), 0);
        Assert.assertEquals(3.1206, returned.getAlphaOrientation(), 0.001);
        Assert.assertEquals(0, returned.getBetaOrientation(), 0.001);
        Assert.assertEquals(6.2639, returned.getGammaOrientation(), 0.001);

    }

    @Test
    public void updateInverseKinematicsModelWheneverThereIsAReceiveConfirmation() throws NoSuchFieldException {
        viewModel.onButtonToggled(true);
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        // after 1 sensor reading, one model was submitted
        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        Assert.assertEquals(1, inverseKinematicsModels.size());

        // after another sensor reading, there's no extra submissions because there's no confirmation that the
        // message has been received
        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        Assert.assertEquals(1, inverseKinematicsModels.size());

        // after confirmation and a new sensor reading, another model is submitted
        viewModel.getOnMessageSent().run();
        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        Assert.assertEquals(2, inverseKinematicsModels.size());
    }
}