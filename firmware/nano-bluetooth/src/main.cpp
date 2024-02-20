#include <ArduinoBLE.h>

#define LED 21 
#define PAIRING_BUTTON 2 
#define RX_PIN 2 

<<<<<<< Updated upstream
//SoftwareSerial bluetooth()
=======
SoftwareSerial bluetooth()
>>>>>>> Stashed changes

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