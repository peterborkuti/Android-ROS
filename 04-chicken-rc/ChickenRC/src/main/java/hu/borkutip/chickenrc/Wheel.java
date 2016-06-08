package hu.borkutip.chickenrc;

/**
 * Created by peter on 2016.06.07..
 */
public class Wheel {
    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;

    public static final int SPEED_LIMIT = 255;
    // speed will be 0 between -MIN_SPEED and +MIN_SPEED
    public static final int MIN_SPEED = 50;
    public static final float ACCELERATION_LIMIT = 6f;
    public final int speed;
    public final int direction;

    public Wheel(float acceleration) {
        direction = (acceleration < 0) ? FORWARD : BACKWARD;

        acceleration = Math.abs(acceleration);
        if (acceleration > ACCELERATION_LIMIT) {
            acceleration = ACCELERATION_LIMIT;
        }

        double spd = Math.floor((SPEED_LIMIT / ACCELERATION_LIMIT) * acceleration);
        if (spd < MIN_SPEED) {
            spd = 0;
        }

        speed = (int)spd;
    }

    public static String toHex(int num) {
        String hex = "00" + Integer.toHexString(num).toUpperCase();
        return hex.substring(hex.length() - 2);
    }

    public String getCommand() {
        return getDirectionLetter() + toHex(speed);
    }

    public String getDirectionLetter() {
        return ((direction == FORWARD) ? "F" : "B");
    }

    @Override
    public String toString() {
        return "Wheel{" + getDirectionLetter() + speed + '}';
    }
}
