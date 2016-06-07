/*
  DC Motor Controller for Two Wheeled car
  
  Commands come from Serial port
  Every command starts with two letters, then a number between 0-255 and ends with new line(\n)
  LFnum - Left motor Forward
  LBnum - Left motor Backward
  RFnum - Right motor Forward
  RBnum - Right motor Backward
  S - Stop All
  
  Peter Borkuti, Jun 4th, 2016
 */

/*
 * command code
 * 0 stop all motors
 * 1 move
 */
byte CMD = 0;

/*
 * Motor 
 * 0: left, 1: right
 */
byte MTR = 0;

/*
 * Direction
 * LOW - Forward
 * HIGH - Backward
 */
byte DIR = LOW;

/*
 * Speed
 * 0 - 255
 */

 byte SPD = 0;

 // digital pins for motors' direction: HIGH: backward, LOW: forward
 // 0. : left motor, 1.: right motor 

 int DIR_PINS[] = {7, 8};

 // pwm pins for motors' speed
 // 0. : left motor, 1.: right motor
 int PWM_PINS[] = {5, 6};
 
String inputString = String("");         // a string to hold incoming data
volatile boolean stringComplete = false;  // whether the string is complete

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  pinMode(13, OUTPUT);
  digitalWrite(13, 1);

  for (int pin = 0; pin < 2; pin++) {
    pinMode(DIR_PINS[pin], OUTPUT);
  }
  //for PWM pins no setup required
}

void parseInput() {
    MTR = 0; //left motor
    CMD = 1; //move
    DIR = LOW; //forward
    SPD = 0;

    char motor = inputString.charAt(0);

    if (motor == 'S') {         // STOP command
      CMD = 0;
      return;      
    } else if (motor == 'R') {  // Right motor
      MTR = 1;
    }
    
    char dir = inputString.charAt(1);

    if ( dir == 'B') {
      DIR = HIGH;
    }

    // converting the input hex string
    String num = inputString.substring(2,4);
    char numcarr[3];
    num.toCharArray(numcarr, 3);
    char *ptr;
    long ret;
    ret = strtol(numcarr, &ptr, 16);

    SPD = byte(ret);
}

void stopMotors() {
  for (int pin = 0; pin < 2; pin++) {
    analogWrite(PWM_PINS[pin], 0);
    digitalWrite(DIR_PINS[pin], LOW);
  }
}

void act() {
  if (CMD == 0) {
    stopMotors();
    return;
  }

  analogWrite(PWM_PINS[MTR], (DIR == LOW) ? SPD : 255 - SPD);
  digitalWrite(DIR_PINS[MTR], DIR);
}

void loop() {
  // print the string when a newline arrives:
  if (stringComplete) {
    parseInput();
    inputString = "";
    stringComplete = false;
    testParse();
    act();
  }
}

void testParse() {
  Serial.print(CMD);
  Serial.print(',');
  Serial.print(MTR);
  Serial.print(',');
  Serial.print(DIR);
  Serial.print(',');
  Serial.println(SPD);
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
