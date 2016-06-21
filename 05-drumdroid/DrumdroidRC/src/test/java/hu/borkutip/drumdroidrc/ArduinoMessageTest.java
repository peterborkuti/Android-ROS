package hu.borkutip.drumdroidrc;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by peter on 2016.06.06..
 *
 *  x : turn, >0 left, <0 right
 *  y : speed, >0 back, <0 forward
 *
 *  I.   x>0, y>0, backward, to left
 *  II.  x>0, y<0, forward, to left
 *  III. x<0, y<0, forward, to right
 *  IV.  x<0, y>0, backward, to right
 */
public class ArduinoMessageTest {

    @Test
    public void testCodeForwardBackward() throws Exception {
        assertEquals("LBFF;RBFF", ArduinoMessage.code(0, Wheel.ACCELERATION_LIMIT));
        assertEquals("LFFF;RFFF", ArduinoMessage.code(0, -Wheel.ACCELERATION_LIMIT));
        assertEquals("LB00;RB00", ArduinoMessage.code(0, 0));
        assertEquals("LB7F;RB7F", ArduinoMessage.code(0, Wheel.ACCELERATION_LIMIT / 2));
        assertEquals("LF7F;RF7F", ArduinoMessage.code(0, -Wheel.ACCELERATION_LIMIT / 2));
    }

    @Test
    public void testCodeTurnOnly() throws Exception {
        assertEquals("LB7F;RF7F", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT, 0));
        assertEquals("LF7F;RB7F", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT, 0));
        assertEquals("LB3F;RF3F", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT / 2, 0));
        assertEquals("LF3F;RB3F", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT / 2, 0));
    }

    @Test
    public void testCodeTurn() throws Exception {
        assertEquals("backward, left", "LB00;RBFF", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT, Wheel.ACCELERATION_LIMIT));
        assertEquals("LBFF;RB00", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT, Wheel.ACCELERATION_LIMIT));
        assertEquals("LB7F;RBFF", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT / 2, Wheel.ACCELERATION_LIMIT));
        assertEquals("LBFF;RB7F", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT / 2, Wheel.ACCELERATION_LIMIT));

        assertEquals("LB00;RFFF", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT, -Wheel.ACCELERATION_LIMIT));
        assertEquals("LFFF;RB00", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT, -Wheel.ACCELERATION_LIMIT));
        assertEquals("LF7F;RFFF", ArduinoMessage.code(Wheel.ACCELERATION_LIMIT / 2, -Wheel.ACCELERATION_LIMIT));
        assertEquals("LFFF;RF7F", ArduinoMessage.code(-Wheel.ACCELERATION_LIMIT / 2, -Wheel.ACCELERATION_LIMIT));
    }
}