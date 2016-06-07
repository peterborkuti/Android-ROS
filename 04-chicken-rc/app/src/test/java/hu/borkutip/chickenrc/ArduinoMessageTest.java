package hu.borkutip.chickenrc;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by peter on 2016.06.06..
 */
public class ArduinoMessageTest {

    @Test
    public void testToHex() throws Exception {
        assertEquals("00", ArduinoMessage.toHex(0));
        assertEquals("09", ArduinoMessage.toHex(9));
        assertEquals("FF", ArduinoMessage.toHex(255));
    }

    @Test
    public void testCode() throws Exception {
        assertEquals("S", ArduinoMessage.code(0, 0));
        assertEquals( "LFFF;RFFF", ArduinoMessage.code(0, 6));
        assertEquals("LBFF;RBFF", ArduinoMessage.code(0, -6));
    }

    /**
     * ( y < 0) : forward, y > 0: backward
     * @throws Exception
     */
    @Test
    public void testLeftWheelDirection() throws Exception {
        final boolean wheel = true; // left wheel
        final int onlyDirection = ArduinoMessage.RETURN_TYPE_DIRECTION;
        assertEquals(
                "straight ahead, forward, left wheel",
                ArduinoMessage.FORWARD,
                ArduinoMessage.codeOneWheel(0, -255, wheel, onlyDirection)
                );

        assertEquals(
                "straight ahead, backward, left wheel",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(0, 255, wheel, onlyDirection)
                );
        assertEquals(
                "To left, forward, left wheel",
                ArduinoMessage.FORWARD,
                ArduinoMessage.codeOneWheel(255, -255, wheel, onlyDirection)
                );
        assertEquals(
                "To left, forward a bit so it will change direction, left wheel",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(255, -100, wheel, onlyDirection)
                );
        assertEquals(
                "To right, backward, left wheel, direction should not changed",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(-255, 255, wheel, onlyDirection)
                );
        assertEquals(
                "To right, backward a bit, left wheel, direction should be changed",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(-255, 100, wheel, onlyDirection)
                );
    }

    @Test
    public void testRightWheelDirection() throws Exception {
        final boolean wheel = false; // right wheel
        final int onlyDirection = ArduinoMessage.RETURN_TYPE_DIRECTION;
        assertEquals(
                "straight ahead, forward, right wheel",
                ArduinoMessage.FORWARD,
                ArduinoMessage.codeOneWheel(0, -255, wheel, onlyDirection)
        );

        assertEquals(
                "straight ahead, backward, right wheel",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(0, 255, wheel, onlyDirection)
        );

        assertEquals(
                "To left, forward, right wheel",
                ArduinoMessage.FORWARD,
                ArduinoMessage.codeOneWheel(255, -255, wheel, onlyDirection)
        );
        assertEquals(
                "To left, forward a bit so it will change direction, right wheel",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(255, -100, wheel, onlyDirection)
        );
        assertEquals(
                "To right, backward, right wheel, direction should not changed",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(-255, 255, wheel, onlyDirection)
        );
        assertEquals(
                "To right, backward a bit, right wheel, direction should be changed",
                ArduinoMessage.BACKWARD,
                ArduinoMessage.codeOneWheel(-255, 100, wheel, onlyDirection)
        );
    }



    @Test
    public void testCodeOneWheel() throws Exception {

    }
}