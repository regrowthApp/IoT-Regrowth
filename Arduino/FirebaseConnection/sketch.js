// serial communication arduino <-> p5js
// internet connection p5js <-> firebase

let serial; // variable for the serial object
let latestData = "waiting for data"; // variable to hold the data

let AnimalType = "Goat";
let AnimalID = 0;
let AnimalWeight = 0.0;
let Temperature = 0.0;
let Humidity = 0;
let state = 0; // current state, i.e. how many variables recieved

function setup() {
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
  
  //createCanvas(windowWidth, windowHeight);
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
}

function gotOpen() {
  console.log("Serial Port is Open");
}

function gotClose() {
  console.log("Serial Port is Closed");
  latestData = "Serial Port is Closed";
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
  
  //latestData = currentString; // save it to the global variable
  
  if (currentString == "BEGIN")
  {  state++; return; }
  if (state == 1)
    {
      state++;
      AnimalType = currentString;
      return;
    }
  if (state == 2)
    {
      state++;
      AnimalID = currentString;
      return;
    }
  if (state == 3)
    {
      state++;
      AnimalWeight = currentString;
      return;
    }
    if (state == 4)
    {
      state++;
      Temperature = currentString;
      return;
    }
    if (state == 5)
    {
      state++;
      Humidity = currentString;
      return;
    }
  if (currentString == "END")
    {
      state = 0;
      // Uploading to firebase should be done here.
      
  var database = firebase.database();

  var currentdate = new Date(); 
  var meas_date = currentdate.getDate() + "-" + (currentdate.getMonth()+1)  + "-"  + currentdate.getFullYear();
  //var meas_time = currentdate.getHours();
  //console.log(meas_date);
  
  var user_directory = "users/regrowth"; /// CHANGE THIS TO CERTAIN USER
  
  var weather_s = user_directory + "/Environment/" + AnimalType +"/" + meas_date; // path name was Weather.
  if (currentdate.getHours() <= 13) // Morning: 00->13, Evening: 14->23
    weather_s+= "/am";
  else
    weather_s+= "/pm";
  
  var animal_s = user_directory + "/Data/" + AnimalType +"/" + AnimalID + "/" + meas_date;
  
  
    // handle weather
  var weather_ref = database.ref(weather_s);
 weather_ref.get().then((snapshot) => {
  if (snapshot.exists()) { 
    // do nothing
  } else { // create new entry
    var weather_data = { temperature: Temperature, humidity: Humidity }
    weather_ref.set(weather_data); 
  }
 }).catch((error) => {
  console.error(error);
});
  
  
  // handle animal input
  var animal_ref = database.ref(animal_s);
   animal_ref.get().then((snapshot) => {
  if (snapshot.exists()) { 
 // increase activity by 1
    
    var animal_s_update = animal_s + "/activity";
    database.ref(animal_s_update).set(firebase.database.ServerValue.increment(1));
  } 
     else { // default activity is 1   create new entry with weight
       var animal_data = { weight: AnimalWeight, activity: 1 }
    animal_ref.set(animal_data); 
  }
 }).catch((error) => {
  console.error(error);
});
          
     // return;
    }
}