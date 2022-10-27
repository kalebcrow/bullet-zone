package edu.unh.cs.cs619.bulletzone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

//implementation from: https://demonuts.com/android-shake-detection/
public class ShakeService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    static TankController tankController;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * omStartCommand: Sets up accelerometer sensor and sensor manager
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    /**
     * onSensorEventChanged: Calculates the magnitude of any sensorEvent and determines
     * if it is enough to cause a fire event to be sent via the rest client
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 11) {

            Log.d("ShakeService", "SHAKE DETECTED!");
            tankController.fire();

        }
    }

    /**
     * onAccuracyChanged: Not required for this implementation
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //no need to implement for sensor like this
    }

    /**
     * setTankContronller: Gives ShakeService access to the client TankController to enable
     * the REST fire message to be triggered by the shake of the device.
     * @param newTankController
     */
    public static void setTankController(TankController newTankController){
        tankController = newTankController;
    }

}
