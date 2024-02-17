#include <Arduino.h>
#include <LiquidCrystal.h>
#include <Wire.h>
#include <SoftwareSerial.h>

#define PAIRING_BUTTON 6
#define RS 7 
#define ENABLE 8 
#define D4 9 
#define D5 10
#define D6 11 
#define D7 12
#define TEST_LED 4
#define RX_PIN 2 
#define TX_PIN 3 

LiquidCrystal lcd(RS, ENABLE, D4, D5, D6, D7); 
SoftwareSerial bluetooth(RX_PIN, TX_PIN); 

int pairing_requests = 0; 

void setup() 
{
    pinMode(PAIRING_BUTTON, INPUT_PULLUP); 
    pinMode(TEST_LED, OUTPUT); 

    pinMode(RX_PIN, INPUT); 
    pinMode(TX_PIN, OUTPUT); 

    lcd.begin(16, 2); 
    lcd.print("LOCKED OUT"); 
    lcd.setCursor(0, 1);
    lcd.print("DOOR OPENER"); 

    Serial.begin(9600);
    bluetooth.begin(38400); 
}

void loop() 
{
    if (digitalRead(PAIRING_BUTTON) == LOW) {
        lcd.setCursor(0, 0); 
        lcd.print("BLUETOOTH     ");
        lcd.setCursor(0, 1); 
        lcd.print("PAIRING       ");
        
        if (bluetooth.available() > 0) {
            digitalWrite(TEST_LED, HIGH);
            char data = bluetooth.read();
            Serial.print(data);

        } else {
            digitalWrite(TEST_LED, LOW); 
        }
    }
    else {
        lcd.setCursor(0, 0);
        lcd.print("LOCKED OUT    "); 
        lcd.setCursor(0, 1);
        lcd.print("DOOR OPENER   "); 
        
        digitalWrite(TEST_LED, LOW);
    }
}