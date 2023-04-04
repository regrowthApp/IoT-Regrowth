const functions = require("firebase-functions");

// // Create and deploy your first functions
// // https://firebase.google.com/docs/functions/get-started
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const { initializeApp } = require('firebase-admin/app');
const { getStorage } = require('firebase-admin/storage');

var admin = require("firebase-admin");

var serviceAccount = require("./regrowth-c498e-firebase-adminsdk-5ks71-0871b48555.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://regrowth-c498e-default-rtdb.europe-west1.firebasedatabase.app",
    storageBucket: "regrowth-c498e.appspot.com"
});

//initializeApp with your config

// admin.initializeApp(
//     {
//         "client_email": "firebase-adminsdk-5ks71@regrowth-c498e.iam.gserviceaccount.com",
//         "databaseURL": "https://regrowth-c498e-default-rtdb.europe-west1.firebasedatabase.app",
//         "storageBucket": "regrowth-c498e.appspot.com"
//     }
// );

const db = admin.database();

const ref = db.ref("restricted_access/secret_document");


exports.listenToDatabase = functions.database.ref('/users/')
    .onWrite((snapshot, context) => {

        console.log('listenToDatabase:snapshot:', snapshot);
        //data node
        snapshot.after.forEach((user) => {
            var userId = user.key;
            var userData = user.val().Data;
            user.forEach((animal) => {
                // Data node
                if (animal.key == 'Data') {
                    // console.log(animal.val());

                    animal.forEach((animalData) => {
                        var animalName = animalData.key;

                        animalData.forEach((animalIDTrack) => {
                            console.log(animalIDTrack.key);
                            animalIDTrack.forEach((animalActivity) => {

                                //========================== Weight check ==========================
                                // users/pierfrancesco-digiuseppe1/Data/Chicken/098164/12-01-2023/weight
                                // users/pierfrancesco-digiuseppe1/Chicken/thresh_holds/max_weight

                                //max int
                                var maxWeight = Number.MAX_SAFE_INTEGER;

                                var minWeight = Number.MIN_SAFE_INTEGER;
                                try {
                                    var maxWeightD = user.child(animalName).child('thresh_holds').child('max_weight').val();
                                    var minWeightD = user.child(animalName).child('thresh_holds').child('min_weight').val();
                                    //e.g. "38"
                                    //check if it is a number
                                    if (isNaN(maxWeightD)) {
                                        maxWeight = maxWeightD;
                                    } else {
                                        //check if is not empty
                                        if (maxWeightD != '') {
                                            maxWeight = parseInt(maxWeightD);
                                        }
                                    }

                                    if (isNaN(minWeightD)) {
                                        minWeight = minWeightD;
                                    } else {
                                        //check if is not empty
                                        if (minWeightD != '') {
                                            minWeight = parseInt(minWeightD);
                                        }
                                    }

                                } catch (error) {
                                    console.log(error);
                                }
                                var animalID = animalIDTrack.key;
                                var animalWeightString = animalActivity.val().weight;
                                var animalWeight = parseInt(animalWeightString);
                                var animalDateString = animalActivity.key;
                                var animalDate = new Date(animalDateString);
                                var today = new Date();
                                var diff = Math.abs(today.getTime() - animalDate.getTime());
                                var diffDays = Math.ceil(diff / (1000 * 3600 * 24));
                                if (diffDays > 1) {
                                    $notificationID = userId + animalData.key + animalID + 'moreThanOneDay';
                                    $title = 'Activity Issue';
                                    $body = animalName + ' with ID ' + animalID + ' has not been active for more than one day';
                                    sendNotification(userId, $notificationID, $title, $body);
                                }
                                if (animalWeight > maxWeight) {
                                    $notificationID = userId + animalData.key + animalID + 'overWeight';
                                    $title = 'Weight Issue';
                                    $body = animalName + ' with ID ' + animalID + ' is above threshold weight';
                                    sendNotification(userId, $notificationID, $title, $body);
                                }
                                if (animalWeight < minWeight) {
                                    $notificationID = userId + animalData.key + animalID + 'underWeight';
                                    $title = 'Weight Issue';
                                    $body = animalName + ' with ID ' + animalID + ' is below threshold weight';
                                    sendNotification(userId, $notificationID, $title, $body);
                                } else {
                                    console.log('no issues. Animal weight is:' + animalWeightString + ' and animal ID is:' + animalID);
                                }
                                //========================== End of Weight check ==========================

                            });

                        });

                    });
                }
                // Environment node
                if (animal.key == 'Environment') {
                    // console.log(animal.val());

                    animal.forEach((animalData) => {
                        var animalName = animalData.key;

                        animalData.forEach((animalIDTrack) => {
                            console.log(animalIDTrack.key);

                            animalIDTrack.forEach((animalActivity) => {

                                //========================== Temperature check ==========================
                                // users/regrowth/Environment/Goat/18-10-2022/am/temperature
                                // users/pierfrancesco-digiuseppe1/Chicken/thresh_holds/max_temp

                                //max int
                                var maxTemp = Number.MAX_SAFE_INTEGER;
                                var minTemp = Number.MIN_SAFE_INTEGER;
                                try {
                                    var maxTempD = user.child(animalName).child('thresh_holds').child('max_temp').val();
                                    var minTempD = user.child(animalName).child('thresh_holds').child('min_temp').val();
                                    //e.g. "38"
                                    //check if it is a number
                                    if (isNaN(maxTempD)) {
                                        maxTemp = maxTempD;
                                    } else {
                                        //check if is not empty
                                        if (maxTempD != '') {
                                            maxTemp = parseInt(maxTempD);
                                        }
                                    }

                                    if (isNaN(minTempD)) {
                                        minTemp = minTempD;
                                    } else {
                                        //check if is not empty
                                        if (minTempD != '') {
                                            minTemp = parseInt(minTempD);
                                        }
                                    }
                                    var animalTempString = animalActivity.child('am').child('temperature').val();
                                    var animalTemp = parseInt(animalTempString);
                                    if (animalTemp > maxTemp) {
                                        $notificationID = userId + animalData.key + animalID + 'overTemp';
                                        $title = 'Temperature Issue';
                                        $body = 'The ' + animalName + ' farm temperature is too high';
                                        sendNotification(userId, $notificationID, $title, $body);
                                    }
                                    if (animalTemp < minTemp) {
                                        $notificationID = userId + animalData.key + animalID + 'underTemp';
                                        $title = 'Temperature Issue';
                                        $body = 'The ' + animalName + ' farm temperature is too low';
                                        sendNotification(userId, $notificationID, $title, $body);
                                    } else {
                                        console.log('no issues. Animal temperature is:' + animalTempString + ' and animal ID is:' + animalID);
                                    }
                                } catch (error) {
                                    console.log(error);
                                }
                                //========================== End of Temperature check ==========================

                                //========================== Humidity check ==========================
                                ///users/regrowth/Environment/Goat/18-10-2022/am/humidity
                                // users/pierfrancesco-digiuseppe1/Chicken/thresh_holds/max_hum

                                //max int
                                var maxHum = Number.MAX_SAFE_INTEGER;
                                var minHum = Number.MIN_SAFE_INTEGER;
                                try {
                                    var maxHumD = user.child(animalName).child('thresh_holds').child('max_hum').val();
                                    var minHumD = user.child(animalName).child('thresh_holds').child('min_hum').val();
                                    //e.g. "38"
                                    //check if it is a number
                                    if (isNaN(maxHumD)) {
                                        maxHum = maxHumD;
                                    } else {
                                        //check if is not empty
                                        if (maxHumD != '') {
                                            maxHum = parseInt(maxHumD);
                                        }
                                    }

                                    if (isNaN(minHumD)) {
                                        minHum = minHumD;
                                    } else {
                                        //check if is not empty
                                        if (minHumD != '') {
                                            minHum = parseInt(minHumD);
                                        }
                                    }
                                    var animalHumString = animalActivity.child('am').child('humidity').val();
                                    var animalHum = parseInt(animalHumString);
                                    if (animalHum > maxHum) {
                                        $notificationID = userId + animalData.key + animalID + 'overHum';
                                        $title = 'Humidity Issue';
                                        $body = 'The ' + animalName + ' farm humidity is too high';
                                        sendNotification(userId, $notificationID, $title, $body);
                                    }
                                    if (animalHum < minHum) {
                                        $notificationID = userId + animalData.key + animalID + 'underHum';
                                        $title = 'Humidity Issue';
                                        $body = 'The ' + animalName + ' farm humidity is too low';
                                        sendNotification(userId, $notificationID, $title, $body);
                                    } else {
                                        console.log('no issues. Animal humidity is:' + animalHumString + ' and animal ID is:' + animalID);
                                    }
                                }
                                catch (error) {
                                    console.log(error);
                                }
                                //========================== End of Humidity check ==========================
                            });

                        });

                    });
                }
            })
        });
    });

exports.getProfile = functions.https.onRequest((req, res) => {
    //users/${uid}/profile
    var uid = req.query.id;
    console.log('uid');
    console.log(uid);
    var profileRef = admin.database().ref('/users/' + uid + '/profile');
    console.log('profileRef:' + profileRef);
    profileRef.once('value', async (snapshot) => {
        console.log('snapshot.val()');
        console.log(snapshot.val());
        var data = snapshot.val();
        //respond with html page
        await generateHtml(data, res, uid);
        //respond with json
        //res.json(data);
    });
});

async function generateHtml(data, res, UID) {
    console.log('my dataa');
    console.log(data);
    //res.set('Cache-Control', 'public, max-age=300, s-maxage=600');

    var farmImageLink = 'farm';
    var logoImage = 'logo';
    try {

        var farmImageLink = await admin.storage().bucket().file('Profile Pictures/' + UID + '/farm').getSignedUrl({
            action: 'read',
            expires: '03-09-2491'
        });
        var logoImage = await admin.storage().bucket().file('Profile Pictures/' + UID + '/logo').getSignedUrl({
            action: 'read',
            expires: '03-09-2491'
        });
    } catch (error) {
        console.log(error);
    }
    //res.send('Profile Pictures/'+UID+'/farm');
    //res.send(farmImageLink);

    var html = `<!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <title>My Farm Website</title>
      <!-- Link to Bootstrap CSS -->
      <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    </head>
    <body>
    
      <!-- Navbar -->
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="#">
          <img src="`+ logoImage + `" width="30" height="30" class="d-inline-block align-top" alt="Logo">
          `+ data.farm_name + `
        </a>
      </nav>
    
      <!-- Main content -->
      <div class="container mt-3">
        <div class="row">
          <div class="col-md-6">
            <h2>Our Address</h2>
            <p>`+ data.address + `</p>
          </div>
          <div class="col-md-6">
            <h2>Contact Us</h2>
            <p><strong>Phone:</strong> `+ data.phone_number + `</p>
            <p><strong>Email:</strong> `+ data.email + `</p>
          </div>
        </div>
    
        <div class="row mt-3">
          <div class="col-md-6">
            <img src="`+ farmImageLink + `" class="rounded" alt="Farm Image" width="100%">
          </div>
          <div class="col-md-6">
            <img src="`+ logoImage + `" class="img-fluid" alt="Logo Image" width="100%">
          </div>
        </div>
      </div>
    
      <!-- Link to Bootstrap JS -->
      <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
      <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    </body>
    </html>
    `;
    res.send(html);

};

function sendNotification(user, notificationID, title, body) {
    admin.database().ref('/notifications' + '/users/' + user + '/' + notificationID).on('value', (snapshot) => {

        //notification ID egrowthSheep58underWeight,regrowthChicken5doverHum,overTemp
        //{  "body": "Sheep with ID 58 is below threshold weight",
        //   "timestamp": 1666257779953,
        //   "title": "Weight Issue"
        // }

        var isWeightNotification = notificationID.endsWith('overWeight') || notificationID.endsWith('underWeight');
        var isActivityNotification = notificationID.endsWith('moreThanOneDay');


        if (snapshot.exists()) {
            //check if timestamp is today
            var timestamp = snapshot.val().timestamp;
            var today = new Date();
            var date = new Date(timestamp);
            var isToday = date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && date.getFullYear() == today.getFullYear();

            if (isToday && (isWeightNotification || isActivityNotification)) {
                console.log('notification already exists for today');
                return;
            }

        }
        console.log('sending notification:' + notificationID);
        admin.database().ref('/notifications' + '/users/' + user + '/' + notificationID).set({
            title: title,
            body: body,
            timestamp: Date.now()
        });
        //send notification to topic
        var message = {
            notification: {
                title: title,
                body: body
            },
            topic: user
        };
        try {
            var id = admin.messaging().send(message);
            id.then(function (result) {
                console.log('Successfully sent message:', result);
            }).catch(function (error) {
                console.log('Error sending message:', error);
            });
        } catch (error) {
            console.log("Error when sending:" + error);
        }

    });
}
