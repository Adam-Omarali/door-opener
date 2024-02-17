#include <Arduino.h>
#include <LiquidCrystal.h>
#include <Wire.h>

#define PAIRING_BUTTON 6
#define RS 7 
#define ENABLE 8 
#define D4 9 
#define D5 10
#define D6 11 
#define D7 12

LiquidCrystal lcd(RS, ENABLE, D4, D5, D6, D7); 

int pairing_requests = 0; 

void setup() 
{
    pinMode(PAIRING_BUTTON, INPUT_PULLUP); 

    lcd.begin(16, 2); 
    lcd.print("LOCKED OUT"); 
    lcd.setCursor(0, 1);
    lcd.print("DOOR OPENER"); 
}

void loop() 
{
    if (digitalRead(PAIRING_BUTTON) == LOW) {
        lcd.setCursor(0, 0); 
        lcd.print("BLUETOOTH     ");
        lcd.setCursor(0, 1); 
        lcd.print("PAIRING       ");
        
         
    }
    else {
        lcd.setCursor(0, 0);
        lcd.print("LOCKED OUT    "); 
        lcd.setCursor(0, 1);
        lcd.print("DOOR OPENER   "); 
    }
}