#include <UTFT.h> 
#include <URTouch.h>

//==== Page Numbers:
// '0' - main / welcome page
// '1' - choose animal type
// '2' - select animal ID
// '3' - set animal weight
// '4' - set farm temperature
// '5' - set farm humidity 
// '6' - upload via serial page

//==== Creating Objects
UTFT    myGLCD(SSD1289,38,39,40,41); //Parameters should be adjusted to your Display/Schield model
URTouch  myTouch(6, 5, 4, 3, 2);

//==== Defining Variables
extern uint8_t SmallFont[];
extern uint8_t BigFont[];

int x,y; // touch location variables
char currentPage;

// string/id variables
const int maxStrLen = 10;
const String AnimalNames[4] = {"Chicken","Sheep","Goat","Pig"};
int animalID, humidity, currentAnimalType;
double animalWeight, temperature;
char strInput[maxStrLen] = {'\0'};
int currInputLocation = 0;
bool ready = false;

extern unsigned int app_logo[0xE10]; // 3600

void setup() {
// Initial setup
  myGLCD.InitLCD();
  myGLCD.clrScr();
  myTouch.InitTouch();
  myTouch.setPrecision(PREC_MEDIUM);

  DrawPage_0();  // Draws the Home Screen
  currentPage = '0'; // Indicates that we are at Home Screen

  Serial.begin(9600);
  Serial.println("Stam");
  delay(200);

}

void ResetString(){
  for (int i = 0; i < maxStrLen; i++){
    strInput[i] = '\0';
  }
  currInputLocation = 0;
}
void FlushDispString(){
  myGLCD.setColor(0, 0, 0);
  myGLCD.fillRoundRect(20, 45, 300, 85);
}
void  AddCharAndPrint(char c) {
  // This function is called when user is inserting a new character, it adds it to the string
 strInput[currInputLocation] = c;
 currInputLocation++;
 strInput[currInputLocation] = '\0';
 // erase prev. string
  FlushDispString();
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print(strInput, CENTER, 60);
  delay(500); // to avoid double pressing
}
void ResetAllVars()
{
   // called when loading page 0, it resets all variables
  ResetString();
  animalID = 0; humidity = 0; currentAnimalType = 0;
  animalWeight=0; temperature=0; ready = false;
}

void HandleInputButtons(int x, int y)
{
  if (currInputLocation >= maxStrLen - 1) // max. str length is in fact 9, because the last char is '\0'
        return;

  if ((x>=20) && (x<=50) && (y>=90) && (y<=110)) { //0
        AddCharAndPrint('0');
      }
  if ((x>=70) && (x<= 100) && (y>=90) && (y<=110)) { //1
        AddCharAndPrint('1');
      }
        if ((x>=120) && (x<=150) && (y>=90) && (y<=110)) { //2
        AddCharAndPrint('2');
      }
        if ((x>=170) && (x<=200) && (y>=90) && (y<=110)) { //3
        AddCharAndPrint('3');
      }
        if ((x>=220) && (x<=250) && (y>=90) && (y<=110)) { //4
        AddCharAndPrint('4');
      }
        if ((x>=270) && (x<=300) && (y>=90) && (y<=110)) { //5
        AddCharAndPrint('5');
      }

  if ((x>=20) && (x<=50) && (y>=130) && (y<=150)) { //6
        AddCharAndPrint('6');
      }
  if ((x>=70) && (x<= 100) && (y>=130) && (y<=150)) { //7
        AddCharAndPrint('7');
      }
        if ((x>=120) && (x<=150) && (y>=130) && (y<=150)) { //8
        AddCharAndPrint('8');
      }
        if ((x>=170) && (x<=200) && (y>=130) && (y<=150)) { //9
        AddCharAndPrint('9');
      }
        if ((x>=220) && (x<=250) && (y>=130) && (y<=150)) { //.
        AddCharAndPrint('.');
      }
        if ((x>=270) && (x<=300) && (y>=130) && (y<=150)) { //R
        ResetString();
        FlushDispString();
      }
      /*
        if ((x>=20) && (x<=50) && (y>=170) && (y<=190)) { //a
        AddCharAndPrint('a');
      }
  if ((x>=70) && (x<= 100) && (y>=170) && (y<=190)) { //b
        AddCharAndPrint('b');
      }
        if ((x>=120) && (x<=150) && (y>=170) && (y<=190)) { //c
        AddCharAndPrint('c');
      }
        if ((x>=170) && (x<=200) && (y>=170) && (y<=190)) { //d
        AddCharAndPrint('d');
      }
        if ((x>=220) && (x<=250) && (y>=170) && (y<=190)) { //e
        AddCharAndPrint('e');
      }
        if ((x>=270) && (x<=300) && (y>=170) && (y<=190)) { //f
        AddCharAndPrint('f');
      }
*/
}

void DrawInputButtons() {

  // Button - 0
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (20, 90, 50, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (20, 90, 50, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("0", 30, 94);

    // Button - 1
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (70, 90, 100, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (70, 90, 100, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("1", 80, 94);

      // Button - 2
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (120, 90, 150, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (120, 90, 150, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("2", 130, 94);

      // Button - 3
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (170, 90, 200, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (170, 90, 200, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("3", 180, 94);

      // Button - 4
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (220, 90, 250, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (220, 90, 250, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("4", 230, 94);

      // Button - 5
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (270, 90, 300, 110);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (270, 90, 300, 110);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("5", 280, 94);

  // Button - 6
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (20, 130, 50, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (20, 130, 50, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("6", 30, 134);

    // Button - 7
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (70, 130, 100, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (70, 130, 100, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("7", 80, 134);

      // Button - 8
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (120, 130, 150, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (120, 130, 150, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("8", 130, 134);

      // Button - 9
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (170, 130, 200, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (170, 130, 200, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("9", 180, 134);

      // Button - .
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (220, 130, 250, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (220, 130, 250, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print(".", 230, 134);

      // Button - R
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (270, 130, 300, 150);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (270, 130, 300, 150);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("R", 280, 134);
/*
  // Button - a
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (20, 170, 50, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (20, 170, 50, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("a", 30, 174);

    // Button - b
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (70, 170, 100, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (70, 170, 100, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("b", 80, 174);

      // Button - c
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (120, 170, 150, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (120, 170, 150, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("c", 130, 174);

      // Button - d
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (170, 170, 200, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (170, 170, 200, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("d", 180, 174);

      // Button - e
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (220, 170, 250, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (220, 170, 250, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("e", 230, 174);

      // Button - f
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (270, 170, 300, 190);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (270, 170, 300, 190);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("f", 280, 174);
*/
    // Button - Back
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (35, 210, 142, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (35, 210, 142, 230);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Back", 80, 214);

   // Button - Continue
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (178, 210, 285, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (178, 210, 285, 230);
  myGLCD.setFont(SmallFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Continue", 200, 214);

}


// DrawPage_0 - Custom Function
void DrawPage_0() {
  ResetAllVars();

  myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Regrowth", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line
  myGLCD.setColor(255, 255, 255); // Sets color to white

  myGLCD.drawBitmap (130, 78, 60, 60, app_logo);

  // Button - Set animal data
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (35, 175, 285, 215);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (35, 175, 285, 215);
  myGLCD.setFont(BigFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Set animal data", CENTER, 187);

}


void DrawPage_1()
{
    // Title
  myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Set animal data", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line
  myGLCD.setColor(255, 255, 255); // Sets color to white
  //myGLCD.setFont(SmallFont); // Sets the font to small
  //myGLCD.print("stam", CENTER, 41); // Prints the string
  myGLCD.setFont(BigFont);
  myGLCD.print("Select animal type", CENTER, 64);
 

  // Button - Chicken
  myGLCD.setColor(16, 167, 103); // Sets green color
  myGLCD.fillRoundRect (20, 90, 150, 130); // Draws filled rounded rectangle
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.drawRoundRect (20, 90, 150, 130); // Draws rounded rectangle without a fill, so the overall appearance of the button looks like it has a frame
  myGLCD.setFont(BigFont); // Sets the font to big
  myGLCD.setBackColor(16, 167, 103); // Sets the background color of the area where the text will be printed to green, same as the button
  myGLCD.print("Chicken", 30, 102); // Prints the string

   // Button - Sheep
  myGLCD.setColor(16, 167, 103); // Sets green color
  myGLCD.fillRoundRect (170, 90, 300, 130); // Draws filled rounded rectangle
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.drawRoundRect (170, 90, 300, 130); // Draws rounded rectangle without a fill, so the overall appearance of the button looks like it has a frame
  myGLCD.setFont(BigFont); // Sets the font to big
  myGLCD.setBackColor(16, 167, 103); // Sets the background color of the area where the text will be printed to green, same as the button
  myGLCD.print("Sheep", 195, 102); // Prints the string
  
  // Button - Goat
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (20, 140, 150, 180);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (20, 140, 150, 180);
  myGLCD.setFont(BigFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Goat", 55, 152);

  // Button
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (170, 140, 300, 180);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (170, 140, 300, 180);
  myGLCD.setFont(BigFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Pig", 210, 152);


  // Button - Back
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (35, 190, 285, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (35, 190, 285, 230);
  myGLCD.setFont(BigFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Back", CENTER, 202);

}

void DrawPage_2() {

  myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Insert " + AnimalNames[currentAnimalType] + " ID", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line -32
  myGLCD.setColor(255, 255, 255); // Sets color to white
  DrawInputButtons();
}
void DrawPage_3() {
  myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  {
    String s = "";
    if (currentAnimalType != 0)
       s += "Insert ";
       s += AnimalNames[currentAnimalType] + " Weight";
  myGLCD.print(s, CENTER, 10); // Prints the string on the screen
  }
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line -32
  myGLCD.setColor(255, 255, 255); // Sets color to white
  DrawInputButtons();
}
void DrawPage_4() {
   myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Farm Temperature", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line -32
  myGLCD.setColor(255, 255, 255); // Sets color to white
  DrawInputButtons();
}
void DrawPage_5() {
    myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Farm Humidity", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line -32
  myGLCD.setColor(255, 255, 255); // Sets color to white
  DrawInputButtons(); 
}
void DrawPage_6() {
  
  myGLCD.setBackColor(0,0,0); // Sets the background color of the area where the text will be printed to black
  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont); // Sets font to big
  myGLCD.print("Uploading Data", CENTER, 10); // Prints the string on the screen
  myGLCD.setColor(255, 0, 0); // Sets color to red
  myGLCD.drawLine(0,40,319,40); // Draws the red line
  myGLCD.setColor(255, 255, 255); // Sets color to white

  myGLCD.setFont(BigFont);
  myGLCD.print("Please wait a bit", CENTER, 100);
  

 // BEGIN SERIAL UPLOADING
 // do we need spaces?
   Serial.println("BEGIN");
  delay(500);
  Serial.println(AnimalNames[currentAnimalType]);
  delay(500);
  Serial.println(animalID);
  delay(500);
  Serial.println(animalWeight);
  delay(500);
  Serial.println(temperature);
  delay(500);
  Serial.println(humidity);
  delay(500);
  Serial.println("END");
  delay(500);

  myGLCD.setColor(0, 0, 0); // black
  myGLCD.fillRoundRect(20, 60, 300, 140);

  myGLCD.setColor(255, 255, 255); // Sets color to white
  myGLCD.setFont(BigFont);
  myGLCD.print("Transfer Successful!", CENTER, 100);

  ready = true;

      // Button - Back
  myGLCD.setColor(16, 167, 103);
  myGLCD.fillRoundRect (35, 190, 285, 230);
  myGLCD.setColor(255, 255, 255);
  myGLCD.drawRoundRect (35, 190, 285, 230);
  myGLCD.setFont(BigFont);
  myGLCD.setBackColor(16, 167, 103);
  myGLCD.print("Back", CENTER, 202);


}


void HandlePage_0() {
  if (myTouch.dataAvailable()) {
      myTouch.read();
      x=myTouch.getX(); // X coordinate where the screen has been pressed
      y=myTouch.getY(); // Y coordinates where the screen has been pressed

      // Press animal data page
      if ((x>=35) && (x<=285) && (y>=175) && (y<=215)) {
        //drawFrame(35, 140, 285, 180);
        currentPage = '1';
        myGLCD.clrScr();
        DrawPage_1();
      }  
}
}

void HandlePage_1() {
  
  if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();

        // Chicken
    if ((x>=20) && (x<=150) &&(y>=90) && (y<=130)) {
      currentPage = '2';
      currentAnimalType = 0;
      myGLCD.clrScr();
      DrawPage_2();
        }
    if ((x>=170) && (x<=300) &&(y>=90) && (y<=130)) {
      currentPage = '2';
      currentAnimalType = 1;
      myGLCD.clrScr();
      DrawPage_2();
        }
      if ((x>=20) && (x<=150) &&(y>=140) && (y<=180)) {
      currentPage = '2';
      currentAnimalType = 2;
      myGLCD.clrScr();
      DrawPage_2();
        }
              if ((x>=170) && (x<=300) &&(y>=140) && (y<=180)) {
      currentPage = '2';
      currentAnimalType = 3;
      myGLCD.clrScr();
      DrawPage_2();
        }
            // If we press the Back Button
    if ((x>=35) && (x<=285) &&(y>=190) && (y<=230)) {
      //drawFrame(35, 190, 285, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }
    }
} 

void HandlePage_2()
{
    if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();
      

    // If we press the Back Button
    if ((x>=35) && (x<=142) &&(y>=210) && (y<=230)) {
      //drawFrame(35, 210, 142, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }

        else
        {
              // If we press the Continue Button
    if ((x>=178) && (x<=285) &&(y>=210) && (y<=230)) {
      if (currInputLocation == 0) return;
      animalID = atoi(strInput);
      ResetString();
        currentPage = '3';
        myGLCD.clrScr();
        DrawPage_3();
        }

    else {
      HandleInputButtons(x,y);
      }
        }
    }
}


void HandlePage_3() {
    if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();
      

    // If we press the Back Button
    if ((x>=35) && (x<=142) &&(y>=210) && (y<=230)) {
      //drawFrame(35, 210, 142, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }

        else
        {
              // If we press the Continue Button
    if ((x>=178) && (x<=285) &&(y>=210) && (y<=230)) {
      if (currInputLocation == 0) return;
      { String temp = String(strInput);
      animalWeight = temp.toDouble(); }
      ResetString();
        currentPage = '4';
        myGLCD.clrScr();
        DrawPage_4();
        }

    else {
      HandleInputButtons(x,y);
      }
        }
    }

}
void HandlePage_4() {
  
    if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();
      

    // If we press the Back Button
    if ((x>=35) && (x<=142) &&(y>=210) && (y<=230)) {
      //drawFrame(35, 210, 142, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }

        else
        {
              // If we press the Continue Button
    if ((x>=178) && (x<=285) &&(y>=210) && (y<=230)) {
      if (currInputLocation == 0) return;
      { String temp = String(strInput);
      temperature = temp.toDouble(); }
      ResetString();
        currentPage = '5';
        myGLCD.clrScr();
        DrawPage_5();
        }

    else {
      HandleInputButtons(x,y);
      }
        }
    }
}
void HandlePage_5() {
  
    if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();
      

    // If we press the Back Button
    if ((x>=35) && (x<=142) &&(y>=210) && (y<=230)) {
      //drawFrame(35, 210, 142, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }

        else
        {
              // If we press the Continue Button
    if ((x>=178) && (x<=285) &&(y>=210) && (y<=230)) {
      if (currInputLocation == 0) return;
      humidity = atoi(strInput);
      ResetString();
        currentPage = '6';
        myGLCD.clrScr();
        DrawPage_6();
        }

    else {
      HandleInputButtons(x,y);
      }
        }
    }
}

void HandlePage_6() {
    
  if (myTouch.dataAvailable()) {
    myTouch.read();
    x=myTouch.getX();
    y=myTouch.getY();

  

            // If we press the Back Button
    if ((x>=35) && (x<=285) &&(y>=190) && (y<=230) && ready)  {
      //drawFrame(35, 190, 285, 230);
      currentPage = '0'; // Indicates we are at home screen
      myGLCD.clrScr();
      DrawPage_0(); // Draws the home screen
        }
    }
  
}


//========== The loop section ========
void loop() { 
  // Home Screen
  if (currentPage == '0') {
    HandlePage_0();
    }

  // Set animal data
  if (currentPage == '1') {    
    HandlePage_1();
  }
  
  // Set farm data
  if (currentPage == '2') {
    HandlePage_2();
  }

    if (currentPage == '3') {
    HandlePage_3();
  }

    if (currentPage == '4') {
    HandlePage_4();
  }

    if (currentPage == '5') {
    HandlePage_5();
  }

    if (currentPage == '6') {
    HandlePage_6();
  }
}
