const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

//Callback url will be of the format 'www.example.com/myCallbackUrl'
exports.myCallbackUrl = functions.https.onRequest((req, res) => {
  let response = {
    "ResultCode": 0,
    "ResultDesc": "Success"
  };

  //Handle data through the 'req' object as per your app logic.
  let body = req.body,
    payload = JSON.stringify(body),
    id = body.Body.stkCallback.CheckoutRequestID;

  console.log(payload);

  const payloadSend = {
    data: {
      payload,
    },
    topic: id
  };

  /*
    We send the response back to safaricom after sending the push notification since
    firebase http triggered functions terminate after calling 'res.send(), res.redirect() or res.end()'.
    If we send the response to safaricom before sending the push notification to device, 
    a race condition can occur therefore having unexpected results.
  */

  return admin.messaging().send(payloadSend).then(() => {
    //Send response back to safaricom that payload has been received successfully
    res.status(200).json(response);   //res.json() internally calls res.send() after creating the json.
  }).catch(error => {
    console.error(error);
    //Send response back to safaricom that payload has been received successfully
    res.status(200).json(response);
  });
});
