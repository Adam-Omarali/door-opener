//This example code is in the Public Domain (or CC0 licensed, at your option.)
//By Evandro Copercini - 2018
//
//This example creates a bridge between Serial and Classical Bluetooth (SPP)
//and also demonstrate that SerialBT have the same functionalities of a normal Serial

#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#define LED 23 
#define READ_BLUETOOTH_BOARD_STATUS 0
#define OUTPUT_MOTOR_BOARD_STATUS 11
#define TOLERANCE 300

BluetoothSerial SerialBT;
String command; 

void setup() {
  Serial.begin(19200);
  SerialBT.begin("ESP32-BLE"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

  pinMode(LED, OUTPUT); 

  pinMode(READ_BLUETOOTH_BOARD_STATUS, INPUT); 
  pinMode(OUTPUT_MOTOR_BOARD_STATUS, OUTPUT); 
}

// CONNECTION
// BLUETOOTH BOARD -> ENABLES ESP32 BOARD -> ENABLES MOTOR BOARD

void loop() {
  digitalWrite(LED, HIGH); 

  command = "<no command>"; 
  
  if (Serial.available()) {
    SerialBT.write(Serial.read());
  }
  if (SerialBT.available()) {
    char c = SerialBT.read();
    Serial.write(c);
    Serial.println(c);

    command = SerialBT.readString(); 
  }
  
  if (command == "<unlock door>") {
    analogWrite(OUTPUT_MOTOR_BOARD_STATUS, 1023); 
  }
  else if (command == "<lock door>") {
    analogWrite(OUTPUT_MOTOR_BOARD_STATUS, 0);
  }
  else {
    analogWrite(OUTPUT_MOTOR_BOARD_STATUS, 512); 
    Serial.println("no command"); 
  }
}