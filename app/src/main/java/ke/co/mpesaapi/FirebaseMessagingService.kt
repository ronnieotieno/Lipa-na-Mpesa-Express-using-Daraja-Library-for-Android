package ke.co.mpesaapi

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class FirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("MessagingService", remoteMessage.data.toString())

        val payload = remoteMessage.data["payload"]

        val gson = Gson()

        val mpesaResponse = gson.fromJson(payload, MpesaResponse::class.java)

        Log.d("MessagingServiceSecond", mpesaResponse.toString())

        val id = mpesaResponse.Body.stkCallback.CheckoutRequestID

        if (mpesaResponse.Body.stkCallback.ResultCode != 0) {

            val reason = mpesaResponse.Body.stkCallback.ResultDesc

            MainActivity.mpesaListener.sendFailed(reason)
            Log.d("MessagingServiceThird", "Operation Failed")
        } else {
            Log.d("MessagingServiceThird", "Operation Success")

            val list = mpesaResponse.Body.stkCallback.CallbackMetadata.Item

            var receipt = ""
            var date = ""
            var phone = ""
            var amount = ""


            list.forEach { item ->

                if (item.Name == "MpesaReceiptNumber") {
                    receipt = item.Value
                }
                if (item.Name == "TransactionDate") {
                    date = item.Value
                }
                if (item.Name == "PhoneNumber") {
                    phone = item.Value

                }
                if (item.Name == "Amount") {
                    amount = item.Value
                }

            }
            MainActivity.mpesaListener.sendSuccessful(amount, phone, getDate(date), receipt)
            Log.d(
                "MetaData",
                "\nReceipt: $receipt\nDate: ${getDate(date)}\nPhone: $phone\nAmount: $amount"
            )

        }

        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(id)

    }

    private fun getDate(date: String): String {

        return "${date.subSequence(6, 8)} ${date.subSequence(4, 6)} ${
            date.subSequence(
                0,
                4
            )
        } at ${date.subSequence(8, 10)} : ${date.subSequence(10, 12)} : ${
            date.subSequence(
                12,
                14
            )
        }"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

}
