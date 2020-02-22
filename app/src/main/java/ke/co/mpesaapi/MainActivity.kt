package ke.co.mpesaapi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.androidstudy.daraja.model.LNMExpress
import com.androidstudy.daraja.model.LNMResult
import com.androidstudy.daraja.util.TransactionType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var daraja: Daraja

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        daraja = Daraja.with(
            "Uku3wUhDw9z0Otdk2hUAbGZck8ZGILyh", // both Consumer and secret key you get after creating an app on Safaricom sandbox
            "JDjpQBm5HpYwk38b",
            object : DarajaListener<AccessToken> {
                override fun onResult(accessToken: AccessToken) {
                    Log.d(
                        this.javaClass.simpleName,
                        "token here   ${accessToken.access_token}"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "MPESA TOKEN : ${accessToken.access_token}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: String) {
                    Log.d(this.javaClass.simpleName, "Token error $error")
                }
            })

        button.setOnClickListener {
            val phoneNumber1 = phone.text.trim().toString()

            val phoneNumber = phoneNumber1.replace(" ", "")

            val lnmExpress = LNMExpress(
                "174379", //Test credential but shortcode is mostly paybill number, email mpesa businnes fo clarification
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials, you will need to request real ones from safaricom by emailing mpesa businnes
                TransactionType.CustomerPayBillOnline,  // TransactionType.CustomerPayBillOnline  <- Apply any of these two
                "1",
                phoneNumber,
                "174379",
                phoneNumber,
                "http://callbackurl.com/checkout.php", // call back url send back payload info if the transactions went through. Its important inorder to update ui after user has paid, its essential but the service can work without it.
                "001ABC",
                "Goods Payment"
            )


            daraja.requestMPESAExpress(lnmExpress,
                object : DarajaListener<LNMResult> {
                    override fun onResult(lnmResult: LNMResult) {
                        Log.d(
                            this@MainActivity.javaClass.simpleName,
                            "Response here ${lnmResult.ResponseDescription}"
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Response here ${lnmResult.ResponseDescription}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: String) {
                        Log.d(
                            this.javaClass.simpleName,
                            "Error here $error"
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Error here $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

    }
}



