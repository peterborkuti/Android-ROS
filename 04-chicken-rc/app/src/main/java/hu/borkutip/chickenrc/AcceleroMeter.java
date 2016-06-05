package hu.borkutip.chickenrc;

/**
 * Created by peter on 2016.06.05..
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Chicken robot will be moved by the help of accelerometer
 *
 * The phone should be tilt forward/backward to move the chicken forward/backward
 * and tilt right/left to wheel the chicken to right/left
 *
 * The Phone
 * Switch OFF orientation change
 *
 * keep the phone in your hand in portrait mode, holding in horizontal position, you have to see
 * the HUAWEI in normal direction
 *
 * Accelerometer values and Moving the chicken
 * Y negative - go forward
 * Y positive - go backward
 * X positive - go left
 * X negative - go right
 *
 * Y : [-6, +6]
 * X : [-6, +6]
 *
 */
public class AcceleroMeter extends TimerTask implements SensorEventListener {
    private volatile float x,y;

    private Sensor accelSensor;
    private SensorManager sManager;

    private Activity activity;
    private TextView output;
    private MyNode node;

    private Timer timer;

    public AcceleroMeter(Activity activity, TextView output, MyNode node) {
        this.activity = activity;
        this.output = output;
        this.node = node;

        sManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelSensor == null){
            Toast.makeText(activity.getApplicationContext(), "No Accelerometer!", Toast.LENGTH_LONG);
            return;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stop() {
        sManager.unregisterListener(this);
        timer.cancel();
        timer.purge();
        timer = null;
    }

    public void start() {
        sManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);
        timer = new Timer();
        timer.schedule(this, 1000, 1000);
    }

    @Override
    public void run() {
        String message = x + "," + y;
        ViewModifier viewModifier = new ViewModifier(output, message);
        activity.runOnUiThread(viewModifier);
        node.send(message);
    }
}
