#line 2 "sketch.ino"
#include <ArduinoUnit.h>
#include <MsTimer2.h>
#include <Servo.h>

//#define TEST
#define DEBUG 

Servo stick[2];

int const DRUM_PINS[] = {9, 10};
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

typedef volatile char vchar;
const int INPUTLIMIT = 50;
vchar inputString[INPUTLIMIT] = "6;3";

volatile boolean stringComplete = true;  // whether the string is complete
volatile boolean noteNeedsSetup = true;
volatile boolean noteSetupDone = false;

void initNotePlayerVariables() {
    timerCounter = 0;
    hitCounter = 0;
    INDEX[0] = 0;
    INDEX[1] = 0;

    NEXTHIT[0] = -1;
    NEXTHIT[1] = -1;

    strcpy(NOTES[0], "");
    strcpy(NOTES[1], "");
}

bool initNotePlayerCore(int sColon, String notes) {
    if ((sColon < 0) || (sColon > notes.length()) || (notes.charAt(sColon) != ';')) {
      return false;
    }
 
    String ls = notes.substring(0, sColon); 
    ls.toCharArray(NOTES[0], MAXNOTES);
    
    if (ls != "") {
      NEXTHIT[0] = (NOTES[0][0] - '0');
    }

    ls = notes.substring(sColon + 1);

    ls.toCharArray(NOTES[1], MAXNOTES);
    
    if (ls != "") {
      NEXTHIT[1] = (NOTES[1][0] - '0');
    }

    return true;
}

#ifdef TEST
test(testInitNotePlayerVariables) {
  initNotePlayerVariables();
  assertEqual(0, timerCounter);
  assertEqual(0, hitCounter);
  assertEqual(0, INDEX[0]);
  assertEqual(0, INDEX[1]);
  assertEqual((const char*)"", (const char*)NOTES[0]);
  assertEqual((const char*)"", (const char*)NOTES[1]);
  
}


test(testInitNotePlayerCoreParams) {
  assertFalse(initNotePlayerCore(-1, ""));
  assertFalse(initNotePlayerCore(1, ""));
  assertFalse(initNotePlayerCore(1, "x"));
  assertFalse(initNotePlayerCore(2, ";"));
  assertTrue(initNotePlayerCore(1, "x;"));
  assertTrue(initNotePlayerCore(0, ";"));
  assertTrue(initNotePlayerCore(1, "x;x"));
}


test(testInitNotePlayerCore) {
  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(1, "x;y"));
  assertEqual((const char*)"x", (const char*)NOTES[0]);
  assertEqual((const char*)"y", (const char*)NOTES[1]);

  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(0, ";y"));
  assertEqual((const char*)"", (const char*)NOTES[0]);
  assertEqual((const char*)"y", (const char*)NOTES[1]);

  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(1, "x;"));
  assertEqual((const char*)"x", (const char*)NOTES[0]);
  assertEqual((const char*)"", (const char*)NOTES[1]);

  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(0, ";"));
  assertEqual((const char*)"", (const char*)NOTES[0]);
  assertEqual((const char*)"", (const char*)NOTES[1]);

  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(3, "123;xyz"));
  assertEqual((const char*)"123", (const char*)NOTES[0]);
  assertEqual((const char*)"xyz", (const char*)NOTES[1]);

  initNotePlayerVariables();
  assertTrue(initNotePlayerCore(3, "123;xyzw"));
  assertEqual((const char*)"123", (const char*)NOTES[0]);
  assertEqual((const char*)"xyzw", (const char*)NOTES[1]);
  assertNotEqual((const char*)"12", (const char*)NOTES[0]);
  assertNotEqual((const char*)"xyz", (const char*)NOTES[1]);
}

void loop() {
  Test::run();
}

#endif

void debugln(const char * m, const char c) {
  #ifdef DEBUG
  Serial.print(m);
  Serial.println(c);
  #endif 
}

void debugln(const char * m, const char * s) {
  #ifdef DEBUG
  Serial.print(m);
  Serial.println(s);
  #endif 
}

void debugln(const char * m, String s) {
  #ifdef DEBUG
  Serial.print(m);
  Serial.println(s);
  #endif 
}

void debug(const char * s) {
  #ifdef DEBUG
  Serial.print(s);
  #endif 
}

void debug(String s) {
  #ifdef DEBUG
  Serial.print(s);
  #endif 
}

bool initNotePlayer() {
  if (strcmp("", (const char*)inputString) == 0) {
    return false;
  }
 
  char s[INPUTLIMIT + 1] = "";
  strncpy(s, (char *)inputString, INPUTLIMIT);
  s[INPUTLIMIT] = '\0';
  
  debugln("inputString in s:",s);
  String ss = "";
  ss.concat(s);

  debugln("ss:",ss);

  int sColon = ss.indexOf(';');

  if (sColon == -1) {
    return false;
  }
  
  MsTimer2::stop();
  initNotePlayerVariables();
  initNotePlayerCore(sColon, ss);

  MsTimer2::start();
  debugln("NOTES0:",NOTES[0]);
  debugln("NOTES1:",NOTES[1]);

  noteSetupDone = true;

  return true;
}

#ifdef TEST
test(testInitNotePlayerParams) {
  strcpy((char *)inputString, "");
  assertFalse(initNotePlayer());
  strcpy((char *)inputString, "x");
  assertFalse(initNotePlayer());
  strcpy((char *)inputString, "x;");
  assertTrue(initNotePlayer());
  strcpy((char *)inputString, ";x");
  assertTrue(initNotePlayer());
}

test(testInitNotePlayer) {
  strcpy((char *)inputString, ";");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"", (const char*)NOTES[0]);
  assertEqual((const char*)"", (const char*)NOTES[1]);

  strcpy((char *)inputString, "x;y");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"x", (const char*)NOTES[0]);
  assertEqual((const char*)"y", (const char*)NOTES[1]);

  strcpy((char *)inputString, ";y");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"", (const char*)NOTES[0]);
  assertEqual((const char*)"y", (const char*)NOTES[1]);

  strcpy((char *)inputString, "x;");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"x", (const char*)NOTES[0]);
  assertEqual((const char*)"", (const char*)NOTES[1]);

  strcpy((char *)inputString, "123;xyz");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"123", (const char*)NOTES[0]);
  assertEqual((const char*)"xyz", (const char*)NOTES[1]);

  strcpy((char *)inputString, "123;xyzw");
  assertTrue(initNotePlayer());
  assertEqual((const char*)"123", (const char*)NOTES[0]);
  assertEqual((const char*)"xyzw", (const char*)NOTES[1]);
  assertNotEqual((const char*)"12", (const char*)NOTES[0]);
  assertNotEqual((const char*)"xyz", (const char*)NOTES[1]);
}

#endif


void stickController() {
 
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

void setup() {
  Serial.begin(57600);
  MsTimer2::set(200, stickController); // 500ms period

  for (int mtr = 0; mtr < 2; mtr++) {
    stick[mtr].attach(DRUM_PINS[mtr]);
    delay(30);
    stick[mtr].write(SETUP_VAL[mtr][SETUP_RST]);
  }

  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);
}



#ifndef TEST

void loop() {
  //debugln("noteSetupDone:", noteSetupDone ? "true":"false");
  if (noteNeedsSetup && stringComplete) {
    debugln("noteSetupDone:", noteSetupDone ? "true":"false");
    initNotePlayer();
    strcpy((char *)inputString, "");
    stringComplete = false;
    noteNeedsSetup = false;
  }
}

#endif

bool processOneChar(char inChar) {
   debugln(">", inChar);
    if (inChar == 'L') {
      USERWANTSHIT[LEFT] = true;
      return true;
    }
    if (inChar == 'R') {
      USERWANTSHIT[RIGHT] = true;
      return true;
    }

    if (inChar == ';') {
      noteNeedsSetup = true;
      noteSetupDone = false;
    }
    // add it to the inputString:
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
    else {
      int len = strlen((const char *)inputString);
      if (len < INPUTLIMIT) {
        inputString[len] = inChar;
        inputString[len + 1] = '\0';
        
      }
      else {
        stringComplete = true;
        return false;
      }
    }

    return true;
}


#ifdef TEST

boolean serialTestEvent(String chars) {
  if (!noteSetupDone) {
    return false;
  }

  for (int i = 0; i < chars.length() ; i++) {
    processOneChar(chars.charAt(i));
  }

  return true;
}

test(testSerialTestEventReturnValue) {
  noteSetupDone = true;
  assertTrue(serialTestEvent(""));
  noteSetupDone = false;
  assertFalse(serialTestEvent(""));
}

test(testSerialTestEvent) {
  noteSetupDone = true;
  strcpy((char *)inputString, "");
  assertTrue(serialTestEvent(";\n"));
  assertFalse((const bool *)noteSetupDone);
  assertTrue((const bool *)noteNeedsSetup);
  assertEqual(";", (const char *)inputString);


  noteSetupDone = true;
  strcpy((char *)inputString, "");
  assertTrue(serialTestEvent("x;\n"));
  assertFalse((const bool *)noteSetupDone);
  assertTrue((const bool *)noteNeedsSetup);
  assertEqual("x;", (const char *)inputString);

  noteSetupDone = true;
  strcpy((char *)inputString, "");
  assertTrue(serialTestEvent(";y\n"));
  assertFalse((const bool *)noteSetupDone);
  assertTrue((const bool *)noteNeedsSetup);
  assertEqual(";y", (const char *)inputString);

  noteSetupDone = true;
  strcpy((char *)inputString, "");
  assertTrue(serialTestEvent("x;y\n"));
  assertFalse((const bool *)noteSetupDone);
  assertTrue((const bool *)noteNeedsSetup);
  assertEqual("x;y", (const char *)inputString);

}


#endif


#ifdef TEST
test(testProcessOneCharReturn) {
  strcpy((char *)inputString, "");
  assertTrue(processOneChar('L'));
  assertTrue(processOneChar('R'));

  //TODO: Test if inputString is full, should return false

  strcpy((char *)inputString, "01234567");
  assertTrue(processOneChar(';'))
  assertTrue(processOneChar('1'))
}

test(testProcessOneCharInputString) {
  strcpy((char *)inputString, "");
  assertTrue(processOneChar('L'));
  assertTrue(processOneChar('R'));
  assertEqual(0, strcmp((char *)inputString, ""));
  assertTrue(processOneChar('1'));
  assertEqual(0, strcmp((char *)inputString, "1"));
  assertTrue(processOneChar(';'));
  assertEqual(0, strcmp((char *)inputString, "1;"));
  assertTrue(processOneChar('2'));
  assertEqual(0, strcmp((char *)inputString, "1;2"));
}


#endif

void serialEvent() {
  /*
  if (!noteSetupDone) {
    debugln("early back from serialEvent", "");
    return;
  }
  */
  
  while (Serial.available()) {
    if (!processOneChar((char)Serial.read())) {
      return;
    }
  }
}


