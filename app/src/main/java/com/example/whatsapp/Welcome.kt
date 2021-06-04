package com.example.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Welcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)



        Handler().postDelayed(
        {

            val intent= Intent(this,Verifyphone::class.java)
            startActivity(intent)
            finish()

        },2000)
    }
}
