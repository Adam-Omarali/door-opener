#include <ArduinoBLE.h>

#define LED 21 
#define PAIRING_BUTTON 2 

void setup() {
  pinMode(LED, OUTPUT); 
  pinMode(PAIRING_BUTTON, INPUT_PULLUP);
}

void loop() {
  Serial.print("HI");
  digitalWrite(LED, HIGH); 
  /*
  if (digitalRead(PAIRING_BUTTON) == LOW) {
    digitalWrite(LED, HIGH); 
  } else {
    digitalWrite(LED, LOW); 
  }*/
}