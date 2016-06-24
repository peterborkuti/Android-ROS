#include <MsTimer2.h>
#include <Servo.h>

Servo stick[2];

int DRUM_PINS[] = {9, 10};
unsigned long timerCounter = 0;
unsigned long hitCounter = 0;

const byte LEFT = 0;
const byte RIGHT = 1;

bool HIT[] = {false ,false};
bool USERHIT[] = {false ,false};
volatile bool USERWANTSHIT[] = {false ,false};
int INDEX[] = {0 ,0};
int NEXTHIT[] = {0, 0};

const byte MAXNOTES = 30;
char NOTES[][MAXNOTES] = {"", ""};

const byte SETUP_HIT = 0;
const byte SETUP_RST = 1;
byte SETUP_VAL[2][2] = { {78, 70} , {88, 98} };

String inputString = String("6;");         // a string to hold incoming data
volatile boolean stringComplete = false;  // whether the string is complete
volatile boolean noteNeedsSetup = false;

void initNotePlayer() {
  String s = String(inputString.c_str());

  MsTimer2::stop();

  Serial.print("s:");
  Serial.println(s);
  int sColon = s.indexOf(';');
  if (sColon != -1) {
    timerCounter = 0;
    hitCounter = 0;
    INDEX[0] = 0;
    INDEX[1] = 0;

    NEXTHIT[0] = -1;
    NEXTHIT[1] = -1;

    String ls = s.substring(0, sColon);

    ls.toCharArray(NOTES[0], MAXNOTES);
    
    if (ls != "") {
      NEXTHIT[0] = (NOTES[0][0] - '0');
    }

    ls = s.substring(sColon + 1);

    ls.toCharArray(NOTES[1], MAXNOTES);
    
    if (ls != "") {
      NEXTHIT[1] = (NOTES[1][0] - '0');
    }

  };
  noteNeedsSetup = false;
  MsTimer2::start();
  Serial.println(NOTES[0]);
  Serial.println(NOTES[1]);
}

void setup() {
  Serial.begin(57600);
  NEXTHIT[0] = (NOTES[0][0] - '0');
  NEXTHIT[1] = (NOTES[1][0] - '0');
  MsTimer2::set(200, stickController); // 500ms period
  initNotePlayer();

  for (int mtr = 0; mtr < 2; mtr++) {
    stick[mtr].attach(DRUM_PINS[mtr]);
    delay(30);
    stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
  }

  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);
}

void loop() {
  if (noteNeedsSetup && stringComplete) {
    initNotePlayer();
    stringComplete = false;
    inputString = "";
  }
}

void stickController(void) {
 
  for (byte mtr = 0; mtr < 2; mtr++) {
    if (HIT[mtr] || USERHIT[mtr]) {
      stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
 
      HIT[mtr] = false;
      USERHIT[mtr] = false;

/*
          Serial.print(mtr);
          Serial.print(",");
          Serial.print(hitCounter);
          Serial.println(",U");
*/
    }
    else if (hitCounter == NEXTHIT[mtr]) {
      stick[mtr].write(SETUP_VAL[mtr][SETUP_HIT]);
 
      INDEX[mtr]++;
 
      if (NOTES[mtr][INDEX[mtr]] == '\0') {
        INDEX[mtr] = 0;
      }
 
      NEXTHIT[mtr] = (NOTES[mtr][INDEX[mtr]] - '0') + hitCounter;
      HIT[mtr] = true;

          Serial.print(mtr);
          Serial.print(",");
          Serial.print(hitCounter);
          Serial.println(",D");

    }
    else if (USERWANTSHIT[mtr] && ((timerCounter % 2) == 0)) {
      stick[mtr].write(SETUP_VAL[mtr][SETUP_HIT]);
      USERWANTSHIT[mtr] = false;
      USERHIT[mtr] = true;

          Serial.print(mtr);
          Serial.print(",");
          Serial.print(hitCounter);
          Serial.println(",UH");
    }
  }
 
  timerCounter++;

  if ((timerCounter % 2) == 0) {
    hitCounter++;
  }
}

void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read();
    if (inChar == 'L') {
      USERWANTSHIT[LEFT] = true;
      continue;
    }
    if (inChar == 'R') {
      USERWANTSHIT[RIGHT] = true;
      continue;
    }

    if (inChar == ';') {
      noteNeedsSetup = true;
    }
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

