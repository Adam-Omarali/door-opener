#include <Arduino.h>
#include <Stepper.h> 
#include "gyro.cpp"

#define INTERBOARD_ANALOG A0
#define TOLERANCE 23
#define TEST_LED 13

// change this to the number of steps on your motor
const int STEPS = 200;

// create an instance of the stepper class, specifying
// the number of steps of the motor and the pins it's
// attached to
Stepper stepper(STEPS, 4, 5, 6, 7);

void setup()
{
  angle_setup();
  Serial.begin(9600);
  // set the speed of the motor to 30 RPMs
  stepper.setSpeed(80);

  pinMode(INTERBOARD_ANALOG, INPUT); 
  pinMode(TEST_LED, OUTPUT);

  digitalWrite(TEST_LED, LOW); 
}

void loop()
{
  get_angle();
  Serial.println("Forward");
  // stepper.step(STEPS * 10);

  // if (analogRead(INTERBOARD_ANALOG) >= 1023 - TOLERANCE) {
  //   digitalWrite(TEST_LED, HIGH); 

  //   Serial.println("Forward");
  //   stepper.step(STEPS * 10);
    
  // } else {
  //   digitalWrite(TEST_LED, LOW); 
  // }
}