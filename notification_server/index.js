const { initializeApp } = require('firebase-admin/app');

var admin = require("firebase-admin");

var serviceAccount = require("./regrowth-c498e-firebase-adminsdk-5ks71-0871b48555.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://regrowth-c498e-default-rtdb.europe-west1.firebasedatabase.app"
});

const db = admin.database();

const ref = db.ref("restricted_access/secret_document");




//listen for realtime database changes
admin.database().ref('/users/').on('value', (snapshot) => {
    // console.log(snapshot.val());
    snapshot.forEach((user) => {
        var userId = user.key;
        var userData = user.val().Data;
        // console.log(user.Data.val());
        user.forEach((animal) => {
            if (animal.key == 'Data') {
                // console.log(animal.val());

                animal.forEach((animalData) => {
                    var animalName = animalData.key;

                    animalData.forEach((animalIDTrack) => {
                        console.log(animalIDTrack.key);
                        animalIDTrack.forEach((animalActivity) => {
                            //console.log(animalActivity.val().weight);
                            var maxWeight = 60;
                            var minWeight = 10;
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
                        });

                    });

                });
            }
        })
    });
}
);

function sendNotification(user, notificationID, title, body) {
    admin.database().ref('/notifications' + '/users/' + user + '/' + notificationID).on('value', (snapshot) => {
        if (snapshot.exists()) {

            console.log('notification already exists');
        } else {
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
                var id =  admin.messaging().send(message);
                id.then(function (result) {
                    console.log('Successfully sent message:', result);
                }).catch(function (error) {
                    console.log('Error sending message:', error);
                });
            } catch (error) {
                console.log("Error when sending:" + error);
            }
        }
    });
}