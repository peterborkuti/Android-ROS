package hu.borkutip.chickenrc;

import java.security.InvalidParameterException;

/**
 * Created by peter on 2016.06.05..
 */
public class ArduinoMessage {
    public static final String FORWARD = "F";
    public static final String BACKWARD = "B";
    public static final String LEFT = "L";
    public static final String RIGHT = "R";
    public static final String STOP = "S";

    public static final int FW = 0;
    public static final int BW = 1;

    public static final int MAX = 6;
    public static final int MIN = 1;
    public static final float MUL = 255 / MAX;

    public static final int RETURN_TYPE_DIRECTION = 0;
    public static final int RETURN_TYPE_SPEED = 1;
    public static final int RETURN_TYPE_DIRECTION_AND_SPEED = 2;

    public static String toHex(int num) {
        String hex = "00" + Integer.toHexString(num).toUpperCase();
        return hex.substring(hex.length() - 2);
    }

    public static String codeOneWheel(int x, int y, boolean leftWheel, int returnType) {
        if (returnType < 0 || returnType > 2) {
            throw new InvalidParameterException();
        }

        float dx = x / 2;

        if (leftWheel) dx = -dx;

        int num = Math.round(Math.abs(y) + dx);

        int mainDir = (y < 0) ? FW : BW;

        int dir = (num < 0) ? 1 - mainDir : mainDir;

        num = Math.min(Math.abs(num), 255);

        String dirStr = (dir == FW) ? FORWARD : BACKWARD;

        String speed = toHex(num);

        String retVal = "";

        switch (returnType) {
            case RETURN_TYPE_DIRECTION: {
                retVal = dirStr;
                break;
            }

            case RETURN_TYPE_SPEED: {
                retVal = speed;
                break;
            }

            case RETURN_TYPE_DIRECTION_AND_SPEED: {
                retVal = dirStr + speed;
                break;
            }
        }

        return retVal;
    }

    public static String code(float x, float y) {

        if ((Math.abs(x) < MIN) && (Math.abs(y) < MIN)) {
            return STOP;
        }

        int ix = Math.round(x * MUL);
        int iy = Math.round(y * MUL);

        String commandL = "L" + codeOneWheel(ix, iy, true, RETURN_TYPE_DIRECTION_AND_SPEED);
        String commandR = "R" + codeOneWheel(ix, iy, false, RETURN_TYPE_DIRECTION_AND_SPEED);;

        return commandL + ";" + commandR;
    }
}
