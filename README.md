# Android-ROS
ROS apps for Android

## 01-pubsub branch

ROS node on Android

It creates to topics, inputTopic and outputTopic. There is an input on the screen and a Send button. The message in input will be sent to outputTopic and
the message in inputTopic will be displayed on the screen.

## 02-pubsub-usb branch

Communicates with ROS core like 01-pubsub. Additionally, it connetcs to an Arduino attached to the Android via usb port.
It sends messages coming from usb to the outputTopic and sends messages coming from inputTopic to the arduino.

All the "routed" messages are displayed on the screen. The message written into the input field is send both to outputTopic and to the Arduino.

The app stops when orientation is changed, so you had better switch auto-rotation off.

### debugging android via WiFi network

Debugging android app via USB is nearly impossible when there is an Arduino attahced to the Android device via USB, so it is a good idea
to debugging the Android app with WiFi.

On the PC:

connect Android via USB cable to the PC
./adb tcpip 5555
./adb connect 192.168.1.157:5555 (192.168.1.157 is the IP of the Android device)
disconnect the usb cable

In Android Studio

Run -> edit configuration
Defaults -> Remote
Set Host to your Android device's IP

### Code for the Arduino:

```
#include <TimerOne.h>

String inputString = String("");         // a string to hold incoming data
volatile boolean stringComplete = false;  // whether the string is complete

String msg = "arduino - ";
long counter = 0;

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // reserve 200 bytes for the inputString:
  inputString.reserve(200);
  pinMode(13, OUTPUT);
  digitalWrite(13, 1);
  Timer1.initialize(5000000);
  Timer1.attachInterrupt(sendMsg);
}

void loop() {
  // print the string when a newline arrives:
  if (stringComplete) {
    char c = inputString.charAt(0);
    digitalWrite(13, (c != '0'));
    // clear the string:
    inputString = "";
    stringComplete = false;
  }
}

void sendMsg(void) {
  Serial.println(msg + counter);
  counter++;
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

```

The incoming USB messages must be ended with "\n" (new line).
Baud rate is 9600

The incoming messages turn on and off the LED on pint 13, a message "0\n" or empty message switches it off,  other messages switches it on.

The output messages are "arduino - x" where x is a counter and they are sent in every 5 seconds.