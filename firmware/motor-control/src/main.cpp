#include <Arduino.h>
#include <Stepper.h> 
#include <SPI.h>
#include "gyro.cpp"

#define INTERBOARD_ANALOG A0
#define UNLOCK_STATUS_FROM_ESP32 A1
#define LOCK_STATUS_FROM_ESP32 A2
#define MOTOR_STATUS_TOLERANCE 150
#define TOLERANCE 23
#define TEST_LED 9
#define UNLOCK_DIGITAL_PIN 11
#define LOCK_DIGITAL_PIN 12 

// change this to the number of steps on your motor
const int STEPS = 200;

// create an instance of the stepper class, specifying
// the number of steps of the motor and the pins it's
// attached to
Stepper stepper(STEPS, 4, 5, 6, 7);

bool max_reverse = true; 
bool max_forward = false; 
char str[50];
volatile byte i;
volatile bool pin;

void setup()
{
  // angle_setup();
  Serial.begin(9600);
  Serial.print("MOSI Pin: ");
  Serial.println(MOSI);
  Serial.print("MISO Pin: ");
  Serial.println(MISO);
  Serial.print("SCK Pin: ");
  Serial.println(SCK);
  Serial.print("SS Pin: ");
  Serial.println(SS);  
  // set the speed of the motor to 30 RPMs
  stepper.setSpeed(40);

  pinMode(UNLOCK_DIGITAL_PIN, INPUT); 
  pinMode(LOCK_DIGITAL_PIN, INPUT); 

  // pinMode(INTERBOARD_ANALOG, INPUT); 
  // pinMode(TEST_LED, OUTPUT);
  pinMode(UNLOCK_STATUS_FROM_ESP32, INPUT); 
  pinMode(LOCK_STATUS_FROM_ESP32, INPUT); 
  SPI.begin();
  pinMode(MISO,OUTPUT);                   //Sets MISO as OUTPUT (Have to Send data to Master IN 
  SPCR |= _BV(SPE);        // turn on SPI in slave mode
  i = 0; // buffer empty
  pin = false;
  SPI.attachInterrupt();     // turn on interrupt
}

ISR(SPI_STC_vect) 
{
  Serial.println("hi");
  char c = SPDR;        // read byte from SPI Data Register
  if (i < sizeof(str)) {
    str [i++] = c; // save data in the next index in the array buff
    if ( (c == '\r') || (c == '\n') || (c=='\0') ) //check for the end of the word
      pin = true;
  }
}

void loop()
{
  Serial.println("UNLOCK " + digitalRead(UNLOCK_DIGITAL_PIN)); 
  Serial.println("LOCK " + digitalRead(LOCK_DIGITAL_PIN)); 

  delay(1000);

  if (digitalRead(UNLOCK_DIGITAL_PIN) == HIGH && digitalRead(LOCK_DIGITAL_PIN) == LOW) {
    stepper.step(STEPS);
  }

  else if (digitalRead(UNLOCK_DIGITAL_PIN) == LOW && digitalRead(LOCK_DIGITAL_PIN) == HIGH) {
    stepper.step(-STEPS); 
  }

  else {
    stepper.step(0);
  }
  // get_angle();
  // Serial.println("Forward");
  // stepper.step(STEPS);
  get_angle();
  Serial.println("Forward");
  stepper.step(STEPS * 10);

  if (analogRead(INTERBOARD_ANALOG) >= 1023 - TOLERANCE) {
    digitalWrite(TEST_LED, HIGH); 

  //   Serial.println("Forward");
  //   stepper.step(STEPS * 10);
  // }
    
  // // } else {
  // //   digitalWrite(TEST_LED, LOW); 
  // // }
  // Serial.print("Lock: ");
  // Serial.println(analogRead(LOCK_STATUS_FROM_ESP32));
  // Serial.print("Unlock: ");
  // Serial.println(analogRead(UNLOCK_STATUS_FROM_ESP32));
  // delay(1000);

  // if (analogRead(MOTOR_STATUS_FROM_ESP32) >= 1023 - MOTOR_STATUS_TOLERANCE) {
  //   // Drive motor forward
  //   stepper.step(STEPS); 
  // }
  // else if (analogRead(MOTOR_STATUS_FROM_ESP32) <= MOTOR_STATUS_TOLERANCE) {
  //   stepper.step(-STEPS); 
  // } 
  // else {
  //   stepper.step(0); 
  // }
    Serial.println("Forward");
    stepper.step(STEPS * 10);
    
  } else {
    digitalWrite(TEST_LED, LOW); 
  }
}