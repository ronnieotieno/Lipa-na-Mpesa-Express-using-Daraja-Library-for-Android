package ke.co.mpesaapi

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class FirebaseMessagingService :
    FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("MessagingService", remoteMessage.data.toString())

        val payload = remoteMessage.data["payload"]

        val gson = Gson()

        val mpesaResponse = gson.fromJson(payload, MpesaResponse::class.java)

        Log.d("MessagingServiceSecond", mpesaResponse.toString())

        var id = mpesaResponse.Body.stkCallback.CheckoutRequestID

        if (mpesaResponse.Body.stkCallback.ResultCode != 0) {

            var reason = mpesaResponse.Body.stkCallback.ResultDesc

            MainActivity.mpesaListener.sendFailed(reason)
            Log.d("MessagingServiceThird", "Operation Failed")
        } else {
            Log.d("MessagingServiceThird", "Operation Success")

            val list = mpesaResponse.Body.stkCallback.CallbackMetadata.Item

            var receipt = ""
            var date = ""
            var phone = ""
            var amount = ""


            for (item in list) {

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
            MainActivity.mpesaListener.sendSuccesfull(amount, phone, date, receipt)
            Log.d("MetaData", "\nReceipt: $receipt\nDate: $date\nPhone: $phone\nAmount: $amount")
            //Log.d("NewDate", getDate(date.toLong()))
        }

        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(id)

    }

    private fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timestamp
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        val date = sdf.format(calendar)
        return date
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

}
