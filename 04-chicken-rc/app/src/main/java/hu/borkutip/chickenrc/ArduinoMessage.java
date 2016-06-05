package hu.borkutip.chickenrc;

/**
 * Created by peter on 2016.06.05..
 */
public class ArduinoMessage {
    public static String FORWARD = "F";
    public static String BACKWARD = "B";
    public static String LEFT = "L";
    public static String RIGHT = "R";

    public static int MAX = 6;
    public static int MIN = 1;
    public static float MUL = 255 / MAX;

    public static String code(float x, float y) {
        String dir = (y < 0) ? FORWARD : BACKWARD;

        int spd = Math.round(MUL * y);
        if (Math.abs(spd) > 255) { spd = (int)Math.signum(spd) * 255; }

        int diff = (int)Math.abs(x * MUL / 2);

        


    }
}
