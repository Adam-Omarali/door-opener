#include <Arduino.h>
#include <Stepper.h> 

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
  Serial.begin(9600);
  // set the speed of the motor to 30 RPMs
  stepper.setSpeed(80);

  pinMode(INTERBOARD_ANALOG, INPUT); 
  pinMode(TEST_LED, OUTPUT);

  digitalWrite(TEST_LED, LOW); 
}


void loop()
{
  Serial.println("Forward");
  stepper.step(STEPS * 10);

  if (analogRead(INTERBOARD_ANALOG) >= 1023 - TOLERANCE) {
    digitalWrite(TEST_LED, HIGH); 

    Serial.println("Forward");
    stepper.step(STEPS * 10);
    
  } else {
    digitalWrite(TEST_LED, LOW); 
  }
}
// #define STEPS 200
// // Define stepper motor connections and motor interface type. Motor interface type must be set to 1 when using a driver

// #define motorInterfaceType 1
// Stepper stepper(STEPS, 2, 3); // Pin 2 connected to DIRECTION & Pin 3 connected to STEP Pin of Driver

// int Pval = 0;

// int potVal = 0;


// void setup() {
//   // Set the maximum speed in steps per second:
//   Serial.begin(9600);

//   stepper.setSpeed(1000);
// }

// void loop() {
//   stepper.step(10);
  // potVal = map(analogRead(A0),0,1024,0,500);
  // if (potVal>Pval){
  //   stepper.step(10);
  //   Serial.println("forward");
  // }
  
  //Serial.println(potVal);

  //if (potVal<Pval){
  
  //Serial.println(potVal);

  //if (potVal<Pval){
  //  stepper.step(10);
    //Serial.print("backward");
  //}


// Pval = potVal;
// }