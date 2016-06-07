package hu.borkutip.chickenrc;

import java.security.InvalidParameterException;

/**
 * Created by peter on 2016.06.05..
 */
public class ArduinoMessage {

    public static String code(float x, float y) {
        float leftWheelAccel;
        float rightWheelAccel;

        if ((x < 0) && (y > 0)) {
            leftWheelAccel = y - x / 2;
            rightWheelAccel = y + x / 2;
        }
        else {
            leftWheelAccel = y + x / 2;
            rightWheelAccel = y - x / 2;
        }

        float supAccel = Math.abs(rightWheelAccel) - Wheel.ACCELERATION_LIMIT;
        if (supAccel > 0) {
            rightWheelAccel = Math.signum(rightWheelAccel) * Wheel.ACCELERATION_LIMIT;
            if (rightWheelAccel > 0) {
                leftWheelAccel -= supAccel;
            }
            else {
                leftWheelAccel += supAccel;
            }
        }
        else {
            supAccel = Math.abs(leftWheelAccel) - Wheel.ACCELERATION_LIMIT;
            if (supAccel > 0) {
                leftWheelAccel = Math.signum(leftWheelAccel) * Wheel.ACCELERATION_LIMIT;
                if (leftWheelAccel > 0) {
                    rightWheelAccel -= supAccel;
                } else {
                    rightWheelAccel += supAccel;
                }
            }
        }

        Wheel rWheel = new Wheel(rightWheelAccel);
        Wheel lWheel = new Wheel(leftWheelAccel);

        return  "L" + lWheel.getCommand() + ";" + "R" + rWheel.getCommand();
    }
}
