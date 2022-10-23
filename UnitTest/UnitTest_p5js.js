// serial communication arduino <-> p5js
// internet connection p5js <-> firebase

// The .html file is the same one found in arduino folder
// change port name in line 17, as shows in p5.serialcontrol app

let serial; // variable for the serial object
let latestData = "waiting for data"; // variable to hold the data

function setup() {

  // serial constructor
  serial = new p5.SerialPort();
  // get a list of all connected serial devices
  serial.list();
  // serial port to use - you'll need to change this
  serial.open('COM5');
  // callback for when the sketchs connects to the server
  serial.on('connected', serverConnected);
  // callback to print the list of serial devices
  serial.on('list', gotList);
  // what to do when we get serial data
  serial.on('data', gotData);
  // what to do when there's an error
  serial.on('error', gotError);
  // when to do when the serial port opens
  serial.on('open', gotOpen);
  // what to do when the port closes
  serial.on('close', gotClose);
	
	try {
            var config  = {
    apiKey: "AIzaSyBNwKnkkd0P0srmjdx0SaR_tkP3YYr1Gis",
    authDomain: "regrowth-c498e.firebaseapp.com",
databaseURL: "https://regrowth-c498e-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "regrowth-c498e",
    storageBucket: "regrowth-c498e.appspot.com",
    messagingSenderId: "248185106763",
    appId: "1:248185106763:web:f263c6ab8206d931d878ce",
    measurementId: "G-L07MD8W03F"
  };
  
  firebase.initializeApp(config);
    var database = firebase.database();
	console.log("connection to firebase successful");
	}
  catch(error)
  {
	  console.log("connection to firebase not successful");
  }
  

}

function serverConnected() {
  console.log("Connected to Server");
}

// list the ports
function gotList(thelist) {
  console.log("List of Serial Ports:");

  for (let i = 0; i < thelist.length; i++) {
    console.log(i + " " + thelist[i]);
  }
  if (thelist.length == 0)
    console.log("connect arduino");
}

function gotOpen() {
  console.log("Serial Port is Open");
}

function gotClose() {
  console.log("Serial Port is Closed");
  latestData = "Serial Port is Closed";
  console.log("open p5.serial control app");
}

function gotError(theerror) {
  console.log(theerror);
}

// when data is received in the serial buffer

function gotData() {
  let currentString = serial.readLine(); // store the data in a variable
  trim(currentString); // get rid of whitespace
  if (!currentString) return; // if there's nothing in there, ignore it
  console.log(currentString); // print it out
  
  if (currentString == "Stam")
	  console.log("arduino <-> p5js serial connection successful");

}