let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);
const express = require('express');
const bodyParser = require('body-parser');

//Initialize our web App
const app = express();
app.use(bodyParser.json());
app.disable('x-powered-by');


//This is our actual callback url `Format will be www.example.com/api/myCallbackUrl`
app.post('/myCallbackUrl', (req, res) => {
    let response = {
        "ResultCode": 0,
        "ResultDesc": "Success"
    }
    //Send response back to safaricom that payload has been received successfully
    res.status(200).json(response);

      //Then handle data through above received payload as per your app logic.
    let body = req.body;
    let payload = JSON.stringify(body)

      console.log(payload)

    let id =  body.Body.stkCallback.CheckoutRequestID

      const payloadSend = {
            data: {
                payload,
            },
             topic: id
        };

         return admin.messaging().send(payloadSend).catch(error=>{
         console.error(error)
         })


})

exports.api = functions.https.onRequest(app);