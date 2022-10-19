<<<<<<< HEAD
# User's Manual - Simulation


### Motivation

The simulation part of the project aims to provide a proof of concept for the Regrowth team.
It simulates (via an arduino mega device) a sensor that is placed to a farm. The real sensor
gathers animal and fam data, and is supposed to upload the data to a web database (firebase).

Here we did exactly that: The user inputs 5 parameters (of a given farm): Animal type, id, weight
and Farm temperature, humidity. After user input is recieved, the data is uploaded to firebase,
updated immediatly in the app, and a relevant notification is sent to the user if needed.
The simulation provides a proof of concept, and enables quick testing and insertion of animal data.

After inserting the data, the raw data for the project can be found in the firebase: 
https://console.firebase.google.com/u/0/project/regrowth-c498e/database/regrowth-c498e-default-rtdb/data


### RELEVANT FOLDERS

In the github there are 2 folders of hardware interest: Arduino, and Unit tests.


### PREREQUISITES

(-) Download desktop application and server for p5.serialport.js, using this link:
https://github.com/p5-serial/p5.serialcontrol/releases.
Choose latest version (0.1.2) -> Assets -> p5.serialcontrol-win32-x64.zip
Download and unzip the directory. The p5.serialcontrol application should be ready to use.


### BASIC CONFIGURATION

(1) The .ino code runs on Arduino Mega board, with 320*240 pixels touch screen. 
The board should be simply plugged into the PC. The PC should have active internet connection.
Note that when uploading to the board, you should download the .ino file and the .c (logo) file,
place them in the same sketch folder, and check board and library compatibility (see 'Extras').

(2) Open the p5.js editor, using the link: https://editor.p5js.org/.
Copy and paste the index.html, sketch.js files respectively into the online editor.
(The style.css file is identical to the default .css file, no need to change it). 

(3) Open the p5.serialcontrol application. Under the 'Info' tab, you should find a list of the available
ports that are actively connected to the PC. Find the appropriate serial port that is used to connect
the arduino mega board (Click rescan ports if necessary).
Copy the port name and replace it in the sketch.js file (in the p5.js online editor), line 33.
Note that the p5.serialcontrol application should stay running in the background and not be closed.

(4) Click the 'Play' button on the p5.js online editor page to start running the script and establish
connection with the board. Only then you can start using the board to simulate the farm sensors and
update the firebase accordingly. When done, click the 'Stop' button, close all applications and remove
the board safely.


### UNIT TESTS

Inside the arduino folder, the Unit tests folder contains simple tests to check for problems resulting
in either the arduino unit, the p5.serialcontrol application or the firebase. The folder contains a .ino
file (arduino IDE) and .js file for p5js editor (use it with arduino/firebase_connection/index.html).


### EXTRAS + LIBRARIES

(-) If you want to edit the .ino code / load it onto another board:
Arduino Mega should be available to use by default package on the Arduino IDE editor. If not, please add
'Arduino Mega or Mega 2560' by following the instructions:
https://support.arduino.cc/hc/en-us/articles/360016119519-Add-boards-to-Arduino-IDE.
(Note: After uploading .ino and .c files to the board, the IDE can be closed safely).

(-) The .ino code uses UTFT.h, URTouch.h libraries by Rinky-Dink Electronics.
Installation links: (Libraries should be imported to Arduino IDE editor)
UTFT: v2.83
http://www.rinkydinkelectronics.com/library.php?id=51
URTouch: v2.02
http://www.rinkydinkelectronics.com/library.php?id=92


### HELPFUL TUTORIALS

A (partial) list of the tutorials that helped us build the hardware section:

(1) https://howtomechatronics.com/tutorials/arduino/arduino-tft-lcd-touch-screen-tutorial/
A youtube tutorial video, explaining basics of a simple arduino application with touch screen

(2) https://www.youtube.com/watch?v=feL_-clJQMs&list=PLnkvii1uWBvEprzkCJZbSAWsiqncWodoQ&index=3
Tutorial video (videos 1+2), explaining how to connect an arduino unit to p5.js via serial connection

(3) https://shiffman.net/a2z/firebase/
How to connect p5.js sketch to firebase (send and store)


### INQUIRIES

For further inquiries, you may contact Kamal Buqai via email: buqaikamal@gmail.com
=======
# User's Manual - Simulation


### Motivation

The simulation part of the project aims to provide a proof of concept for the Regrowth team.
It simulates (via an arduino mega device) a sensor that is placed to a farm. The real sensor
gathers animal and fam data, and is supposed to upload the data to a web database (firebase).

Here we did exactly that: The user inputs 5 parameters (of a given farm): Animal type, id, weight
and Farm temperature, humidity. After user input is recieved, the data is uploaded to firebase,
updated immediatly in the app, and a relevant notification is sent to the user if needed.
The simulation provides a proof of concept, and enables quick testing and insertion of animal data.

After inserting the data, the raw data for the project can be found in the firebase: 
https://console.firebase.google.com/u/0/project/regrowth-c498e/database/regrowth-c498e-default-rtdb/data


### RELEVANT FOLDERS

In the github there are 2 folders of hardware interest: Arduino, and Unit tests.


### PREREQUISITES

(-) Download desktop application and server for p5.serialport.js, using this link:
https://github.com/p5-serial/p5.serialcontrol/releases.
Choose latest version (0.1.2) -> Assets -> p5.serialcontrol-win32-x64.zip
Download and unzip the directory. The p5.serialcontrol application should be ready to use.


### BASIC CONFIGURATION

(1) The .ino code runs on Arduino Mega board, with 320*240 pixels touch screen. 
The board should be simply plugged into the PC. The PC should have active internet connection.
Note that when uploading to the board, you should download the .ino file and the .c (logo) file,
place them in the same sketch folder, and check board and library compatibility (see 'Extras').

(2) Open the p5.js editor, using the link: https://editor.p5js.org/.
Copy and paste the index.html, sketch.js files respectively into the online editor.
(The style.css file is identical to the default .css file, no need to change it). 

(3) Open the p5.serialcontrol application. Under the 'Info' tab, you should find a list of the available
ports that are actively connected to the PC. Find the appropriate serial port that is used to connect
the arduino mega board (Click rescan ports if necessary).
Copy the port name and replace it in the sketch.js file (in the p5.js online editor), line 33.
Note that the p5.serialcontrol application should stay running in the background and not be closed.

(4) Click the 'Play' button on the p5.js online editor page to start running the script and establish
connection with the board. Only then you can start using the board to simulate the farm sensors and
update the firebase accordingly. When done, click the 'Stop' button, close all applications and remove
the board safely.


### UNIT TESTS

Inside the arduino folder, the Unit tests folder contains simple tests to check for problems resulting
in either the arduino unit, the p5.serialcontrol application or the firebase. The folder contains a .ino
file (arduino IDE) and .js file for p5js editor (use it with arduino/firebase_connection/index.html).


### EXTRAS + LIBRARIES

(-) If you want to edit the .ino code / load it onto another board:
Arduino Mega should be available to use by default package on the Arduino IDE editor. If not, please add
'Arduino Mega or Mega 2560' by following the instructions:
https://support.arduino.cc/hc/en-us/articles/360016119519-Add-boards-to-Arduino-IDE.
(Note: After uploading .ino and .c files to the board, the IDE can be closed safely).

(-) The .ino code uses UTFT.h, URTouch.h libraries by Rinky-Dink Electronics.
Installation links: (Libraries should be imported to Arduino IDE editor)
UTFT: v2.83
http://www.rinkydinkelectronics.com/library.php?id=51
URTouch: v2.02
http://www.rinkydinkelectronics.com/library.php?id=92


### HELPFUL TUTORIALS

A (partial) list of the tutorials that helped us build the hardware section:

(1) https://howtomechatronics.com/tutorials/arduino/arduino-tft-lcd-touch-screen-tutorial/
A youtube tutorial video, explaining basics of a simple arduino application with touch screen

(2) https://www.youtube.com/watch?v=feL_-clJQMs&list=PLnkvii1uWBvEprzkCJZbSAWsiqncWodoQ&index=3
Tutorial video (videos 1+2), explaining how to connect an arduino unit to p5.js via serial connection

(3) https://shiffman.net/a2z/firebase/
How to connect p5.js sketch to firebase (send and store)


### INQUIRIES

For further inquiries, you may contact Kamal Buqai via email: buqaikamal@gmail.com
>>>>>>> refs/remotes/origin/main
