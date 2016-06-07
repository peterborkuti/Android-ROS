package hu.borkutip.chickenrc;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by peter on 2016.06.07..
 */
public class WheelTest {

    @Test
    public void testToHex() throws Exception {
        assertEquals("00", Wheel.toHex(0));
        assertEquals("09", Wheel.toHex(9));
        assertEquals("FF", Wheel.toHex(255));
    }

    @Test
    public void testGetCommand() throws Exception {
        Wheel wheel = new Wheel(Wheel.ACCELERATION_LIMIT);
        assertEquals(Wheel.BACKWARD, wheel.direction);
        assertEquals(Wheel.SPEED_LIMIT, wheel.speed);

        wheel = new Wheel(-Wheel.ACCELERATION_LIMIT);
        assertEquals(Wheel.FORWARD, wheel.direction);
        assertEquals(Wheel.SPEED_LIMIT, wheel.speed);

        wheel = new Wheel(Wheel.ACCELERATION_LIMIT/Wheel.SPEED_LIMIT * Wheel.MIN_SPEED - 1);
        assertEquals(Wheel.BACKWARD, wheel.direction);
        assertEquals(0, wheel.speed);

        wheel = new Wheel(-Wheel.ACCELERATION_LIMIT/Wheel.SPEED_LIMIT * Wheel.MIN_SPEED + 1);
        assertEquals(Wheel.FORWARD, wheel.direction);
        assertEquals(0, wheel.speed);

        wheel = new Wheel(Wheel.ACCELERATION_LIMIT/Wheel.SPEED_LIMIT * Wheel.MIN_SPEED);
        assertEquals(Wheel.BACKWARD, wheel.direction);
        assertEquals(Wheel.MIN_SPEED, wheel.speed);

        wheel = new Wheel(-Wheel.ACCELERATION_LIMIT/Wheel.SPEED_LIMIT * Wheel.MIN_SPEED);
        assertEquals(Wheel.FORWARD, wheel.direction);
        assertEquals(Wheel.MIN_SPEED, wheel.speed);

        wheel = new Wheel(0);
        assertEquals(Wheel.BACKWARD, wheel.direction);
        assertEquals(0, wheel.speed);

        wheel = new Wheel(2 * Wheel.ACCELERATION_LIMIT);
        assertEquals(Wheel.BACKWARD, wheel.direction);
        assertEquals(Wheel.SPEED_LIMIT, wheel.speed);

        wheel = new Wheel(- 2 * Wheel.ACCELERATION_LIMIT);
        assertEquals(Wheel.FORWARD, wheel.direction);
        assertEquals(Wheel.SPEED_LIMIT, wheel.speed);
    }
}