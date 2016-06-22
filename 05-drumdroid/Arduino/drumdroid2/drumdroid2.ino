#include <MsTimer2.h>
#include <Servo.h>

Servo stick[2];

int DRUM_PINS[] = {9, 10};
unsigned long timerCounter = 0;
unsigned long hitCounter = 0;

const byte LEFT = 0;
const byte RIGHT = 1;

bool HIT[] = {false ,false};
int INDEX[] = {0 ,0};
int NEXTHIT[] = {0, 0};

char NOTES[][30] = {"1331", "422"};

const byte SETUP_HIT = 0;
const byte SETUP_RST = 1;
byte SETUP_VAL[2][2] = { {78, 70} , {88, 98} };

void setup() {
  NEXTHIT[0] = (NOTES[0][0] - '0');
  NEXTHIT[1] = (NOTES[1][0] - '0');
  MsTimer2::set(200, stickController); // 500ms period
  MsTimer2::start();
  Serial.begin(57600);

  for (int mtr = 0; mtr < 2; mtr++) {
    stick[mtr].attach(DRUM_PINS[mtr]);
    delay(30);
    stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
  }

  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:

}

void stickController(void) {
 
  for (byte mtr = 0; mtr < 2; mtr++) {
    if (HIT[mtr]) {
      stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
 
      HIT[mtr] = false;

      if (mtr == LEFT) {
          //Serial.print(hitCounter);
          //Serial.println(",U");
          digitalWrite(13, LOW);
      }
    }
    else if (hitCounter == NEXTHIT[mtr]) {
      stick[mtr].write(SETUP_VAL[mtr][SETUP_HIT]);
 
      INDEX[mtr]++;
 
      if (NOTES[mtr][INDEX[mtr]] == '\0') {
        INDEX[mtr] = 0;
      }
 
      NEXTHIT[mtr] = (NOTES[mtr][INDEX[mtr]] - '0') + hitCounter;
      HIT[mtr] = true;

      if (mtr == LEFT) {
         // Serial.print(hitCounter);
          //Serial.println(",D");
          digitalWrite(13, HIGH);
      }
    }
  }
 
  timerCounter++;

  if ((timerCounter % 2) == 0) {
    hitCounter++;
  }
}

