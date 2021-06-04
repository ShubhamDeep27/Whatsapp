package com.example.whatsapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Credentials
import android.net.wifi.hotspot2.pps.Credential
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_verifyphone.*
import java.util.concurrent.TimeUnit

class Verifyphone : AppCompatActivity() {




    private var verifyid:String?=null
   private var credential:PhoneAuthCredential?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyphone)



        layout_enter_phone.visibility = View.VISIBLE
        layout_verify.visibility = View.GONE


        skip_btn.setOnClickListener {

            val intent =Intent(this@Verifyphone,Register::class.java)
            startActivity(intent)
            finish()
        }

        sendcode.setOnClickListener {

            val phone = phone_no_.text.toString().trim()
            val phonenumber = '+' + "91" + phone




            if (phone.isNullOrBlank() )
            {
                phone_no_.error="Enter Valid No."
                phone_no_.requestFocus()
                return@setOnClickListener

            }

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(phonenumber, 60, TimeUnit.SECONDS, this, phoneAuthcallbacks)



                layout_enter_phone.visibility=View.GONE
            layout_verify.visibility=View.VISIBLE


        }


        verify.setOnClickListener {


            val code=verification_code.text.toString().trim()
            if (code.isEmpty())
            {
                verification_code.error="Enter Valid Code."
                verification_code.requestFocus()
                return@setOnClickListener

            }
            verifyid.let {
               credential=PhoneAuthProvider.getCredential(this!!.verifyid.toString(),code)

                //addphonenumber(credential)

            }

        }

    }


    val phoneAuthcallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks()


    {


        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {

            phoneAuthCredential.let {
                val progressDialog=ProgressDialog(this@Verifyphone)
                progressDialog.setTitle("Verifiying")
                progressDialog.setMessage("Please Wait......")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val intent =Intent(this@Verifyphone,Register::class.java)
                startActivity(intent)
                finish()
                //addphonenumber(phoneAuthCredential)

            }
            credential.let {
                val progressDialog=ProgressDialog(this@Verifyphone)
                progressDialog.setTitle("Verifiying")
                progressDialog.setMessage("Please Wait......")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val intent =Intent(this@Verifyphone,Register::class.java)
                startActivity(intent)
                finish()
            }
        }

        override fun onVerificationFailed(p0: FirebaseException?) {



            Toast.makeText(this@Verifyphone, p0?.message, Toast.LENGTH_LONG).show()
        }


        override fun onCodeSent(verificationid: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationid, p1)


            Log.d("shit",verificationid)

            verifyid=verificationid


        }


    }


    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser!=null)
        {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}

