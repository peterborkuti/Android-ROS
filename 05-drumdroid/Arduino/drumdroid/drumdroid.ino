/*
  Drum Droid Controller
  
  Commands come from Serial port
  Setup commands
    Starts with two letters, than a hex number
    LHnum - Left stick Hit angle
    LRnum - Left stick Rest angle (starting position)
    RHnum - Right stick Hit angle
    RSnum - Right stick Rest angle (starting position)
    LDnum - Delay after hit
    RDnum - Delay after hit
*/

const byte SETUP_HIT = 0;
const byte SETUP_RST = 1;
const byte SETUP_DLY = 2;

byte SETUP_CMD = 0;

byte SETUP_VAL[2][3] = { {78, 70, 100} , {85, 98, 100} };

//num for setup command
byte NUM = 0;

/*
  Hit commands
    L : left stich start
    R : right hit start
    
  Peter Borkuti, Jun 19th, 2016
  versions
  1.0 Jun 19.,  2016 - initial version, based on dcmotor for chicken and servo examples
 */

#include <Servo.h>

Servo stick[2];  // create servo object to control a servo

const byte NONE = 0;
const byte HIT = 1;
const byte SETUP = 2;
/*
 * command code
 * This is not used at android side, so it is superfluous, but maybe later..
 * 0 start hit
 * 1 setup
 */
 
byte CMD = NONE;

/*
 * Motor 
 * 0: left, 1: right
 */
const byte LEFT = 0;
const byte RIGHT = 1;
byte MTR = 0;

 // digital pins for motors' direction: HIGH: backward, LOW: forward
 // 0. : left motor, 1.: right motor 

 int DRUM_PINS[] = {9, 10};
 
 //Status
 //Actual degree of servos and the elapsed time at that state
 unsigned long DELAY_COUNTER[] = {0, 0};
 const byte STATE_RST = 0;
 const byte STATE_HIT = 1;
 int DRUM_STATE[] = {STATE_RST, STATE_RST};

String inputString = String("");         // a string to hold incoming data
volatile boolean stringComplete = false;  // whether the string is complete

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  pinMode(13, OUTPUT);
  digitalWrite(13, 1);

  for (int mtr = 0; mtr < 2; mtr++) {
    stick[mtr].attach(DRUM_PINS[mtr]);
    delay(30);
    stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
    DRUM_STATE[mtr] = STATE_RST;
  }
}

void parseInput() {

    SETUP_CMD = SETUP_RST;
    CMD = NONE;
    MTR = LEFT;
 
    char motor = inputString.charAt(0);

    if (motor != 'L') {
      MTR = RIGHT;
    }

    if (inputString.length() < 3) {
      CMD = HIT;
      return;
    }

 
    char sc = inputString.charAt(1);

    if (sc == 'H') {
      SETUP_CMD = SETUP_HIT;
          CMD = SETUP;
    }
    else if (sc == 'D') {
      SETUP_CMD = SETUP_DLY;
          CMD = SETUP;
    }
    else if (sc == 'R') {
      SETUP_CMD = SETUP_RST;
          CMD = SETUP;
    }

    if (CMD != SETUP) {
      return;
    }

    // converting the input hex string
    String num = inputString.substring(2,inputString.length());
    char numcarr[10];
    num.toCharArray(numcarr, 10);
    char *ptr;
    long ret;
    ret = strtol(numcarr, &ptr, 10);

    NUM = byte(ret);
}

void setupMotor() {
  SETUP_VAL[MTR][SETUP_CMD] = NUM;
}

void startHit() {
  stick[MTR].write(SETUP_VAL[MTR][SETUP_HIT]);
  DELAY_COUNTER[MTR] = millis();
  DRUM_STATE[MTR] = STATE_HIT;
}

void startRest(int mtr) {
  stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
  DELAY_COUNTER[mtr] = millis();
  DRUM_STATE[mtr] = STATE_RST;
}

void controlMotors() {
  for (byte mtr = 0; mtr < 2; mtr++) {
    if (DRUM_STATE[mtr] == STATE_HIT) {
      if (DELAY_COUNTER[mtr] + SETUP_VAL[mtr][SETUP_DLY] < millis()) {
        startRest(mtr);
      } 
    }
  }
}

void act() {
  if (CMD == SETUP) {
    setupMotor();
    return;
  }

  if (CMD == HIT) {
    startHit();
    return;
  }

  testParse();
}

void loop() {
  // print the string when a newline arrives:
  if (stringComplete) {
    parseInput();
    inputString = "";
    stringComplete = false;
    act();
    testParse();
  }
  controlMotors();
}

void testParse() {
  for (int mtr = 0; mtr < 2; mtr++) {
    Serial.print("Motor:");
    Serial.print(mtr);
    Serial.print(",");
    for (int stp = 0; stp < 3; stp++) {
      Serial.print(SETUP_VAL[mtr][stp]);
      Serial.print(",");
    }
  }
  Serial.println();
  
  Serial.print("status:"); 
  Serial.print(DRUM_STATE[LEFT]);
  Serial.print(',');
  Serial.println(DRUM_STATE[RIGHT]);
}

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
    else {
      inputString = inputString + inChar;
    }
  }
}
