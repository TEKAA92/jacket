#include <Adafruit_NeoPixel.h>

char intBuffer[12];
String intData = "";
int delimiter = (int) '\n';

const int sensorPin0 = A0;
const int sensorPin1 = A1;
const int sensorPin2 = A2;
const int sensorPin3 = A3;
int temp0;
int temp1;
int temp2;
int temp3;
int userHeat;
unsigned int integerValue= 0;
char incomingByte;
long time;
long timeSystem;

#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(60, PIN, NEO_GRB + NEO_KHZ800);

void setup()
{
	Serial.begin(9600);
  //Serial.print("AT+BAUD4"); 
  pinMode(4,OUTPUT);
  strip.begin();
  strip.show();
  timeSystem = millis();
}

void loop() {
  temp0 = getTemp(sensorPin0); //Left arm
  temp1 = getTemp(sensorPin1); //Left side
  temp2 = getTemp(sensorPin2); //Right arm
  temp3 = getTemp(sensorPin3); //Right side
  
  int tempFinal = (temp0 + temp1 + temp2 + temp3) / 4; //Average temp
  
  char input = Serial.read();
  
  if (input == 'a') {
  	while(Serial.read() != 'b') {
  		theaterChase(strip.Color(255, 0, 0), 10);
  		tone(8, 1000, 50);
  	}
  	strip.show();
  }
  
  if (input == 'c') {
  	while (Serial.available() == 0) {
  	}
      integerValue = 0;         // throw away previous integerValue
      while(1) {            // force into a loop until 'n' is received
      incomingByte = Serial.read();
        if (incomingByte == '!') break;   // exit the while(1), we're done receiving
        if (incomingByte == -1) continue;  // if no characters are in the buffer read() returns -1
        integerValue *= 10;  // shift left 1 decimal place
        // convert ASCII to integer, add, and shift left 1 decimal place
        integerValue = ((incomingByte - 48) + integerValue);
    }
  // Do something with the value
  userHeat = integerValue;
}

/*tempFinal*/
  if (tempFinal <= userHeat && userHeat != 0) { //If the average temperature is below 35C, then the heating pads will be turned on
  	digitalWrite(4, HIGH);
  } else {
  	digitalWrite(4, LOW);
  }
  
  time = millis();
  if ((time - timeSystem) >= 1000) {
   
    Serial.print("c" + String(tempFinal) + "!");
    timeSystem = millis();
  }
}

  int getTemp(int pin) {  
  //getting the voltage reading from the temperature sensor
  int reading = analogRead(pin);  

  // converting that reading to voltage, for 3.3v arduino use 3.3
  float voltage = (reading/1024.0) * 5.0; 

  // now print out the temperature
  float temperatureC = (voltage - .5) * 100 ;  //converting from 10 mv per degree wit 500 mV offset
  //to degrees ((voltage - 500mV) times 100)

  return temperatureC;
}

//Theatre-style crawling lights.
void theaterChase(uint32_t c, uint8_t wait) {
  for (int j=0; j<10; j++) {  //do 10 cycles of chasing
  	for (int q=0; q < 3; q++) {
  		for (int i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, c);    //turn every third pixel on
    }
    strip.show();

    delay(wait);

    for (int i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, 0);        //turn every third pixel off
    }
}
}
}
