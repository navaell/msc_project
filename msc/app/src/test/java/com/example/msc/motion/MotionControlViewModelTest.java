package com.example.msc.motion;

import android.hardware.SensorEvent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.msc.models.InverseKinematicsModel;

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

    private List<String> xValues;
    private List<String> yValues;
    private List<String> zValues;
    private List<InverseKinematicsModel> inverseKinematicsModels;

    private final float[] testValues = new float[]{1.1f, 1.2f, 1.3f};

    @Mock
    private SensorEvent sensorEvent;
    private MotionControlViewModel viewModel;

    @Before
    public void setUp() {
        xValues = new LinkedList<>();
        yValues = new LinkedList<>();
        zValues = new LinkedList<>();
        inverseKinematicsModels = new LinkedList<>();

        viewModel = new MotionControlViewModel();

        viewModel.getxLiveData().observeForever(value -> xValues.add(value));
        viewModel.getyLiveData().observeForever(value -> yValues.add(value));
        viewModel.getzLiveData().observeForever(value -> zValues.add(value));
        viewModel.getInverseKinematicsModelLiveData().observeForever(value -> inverseKinematicsModels.add(value));
    }

    @Test
    public void thereIsNoXYZDataBeforeSensorChangedEvent() {
        Assert.assertTrue(xValues.isEmpty());
        Assert.assertTrue(yValues.isEmpty());
        Assert.assertTrue(zValues.isEmpty());
    }

    @Test
    public void onSensorChangedUpdatesX() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(1, xValues.size());
        Assert.assertEquals("-0.470796", xValues.get(0)); // values is offset by pi/2
    }

    @Test
    public void onSensorChangedUpdatesY() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(1, yValues.size());
        Assert.assertEquals("1.200000", yValues.get(0));
    }

    @Test
    public void onSensorChangedUpdatesZ() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);

        Assert.assertEquals(1, zValues.size());
        Assert.assertEquals("1.300000", zValues.get(0));
    }

    @Test
    public void inverseKinematicsModelContainsExpectedData() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        Assert.assertEquals(1, inverseKinematicsModels.size());

        InverseKinematicsModel returned = inverseKinematicsModels.get(0);

        Assert.assertEquals(0.5, returned.getxCoordinate(), 0);
        Assert.assertEquals(0.5, returned.getyCoordinate(), 0);
        Assert.assertEquals(0.5, returned.getzCoordinate(), 0);
        Assert.assertEquals(-0.470796, returned.getAlphaOrientation(), 0.001);
        Assert.assertEquals(1.3, returned.getBetaOrientation(), 0.00001);
        Assert.assertEquals(1.2, returned.getGammaOrientation(), 0.00001);

    }

    @Test
    public void onEvery10thSensorChangedUpdateInverseKinematicsModel() throws NoSuchFieldException {
        FieldSetter.setField(sensorEvent, sensorEvent.getClass().getDeclaredField("values"), testValues);

        for (int i = 0; i < 25; i++) {
            viewModel.getSensorEventListener().onSensorChanged(sensorEvent);
        }
        // expected 3 updates for the inverseKinematicsModel; at i=0, i=10 and i=20
        Assert.assertEquals(3, inverseKinematicsModels.size());
    }
}