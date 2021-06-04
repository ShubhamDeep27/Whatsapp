package com.example.whatsapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    var firebaseUser: FirebaseUser?=null
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val toolbar: Toolbar =findViewById(R.id.toolbar_Login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }





        mAuth=FirebaseAuth.getInstance()
        btn_login.setOnClickListener {

            loginuser()

        }


        createone.setOnClickListener {
            val intent= Intent(this, Verifyphone::class.java)
            startActivity(intent)
            finish()
        }














    }

    private fun loginuser() {
        val progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Login")
        progressDialog.setMessage("Please Wait......")
        progressDialog.setCanceledOnTouchOutside(false)


        val email=email_login.text.toString()
        val password=password_login.text.toString()


        if (email.isEmpty()){
            email_login.error="Enter Email"
            email_login.requestFocus()
            return

        }
        if (password.isEmpty()) {
            password_login.error = "Enter Password"
            password_login.requestFocus()
            return
        }
        progressDialog.show()
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            else
            {
                progressDialog.dismiss()
                Toast.makeText(this,"Error Message :" + task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }




    }

    override fun onStart() {
        super.onStart()
        firebaseUser= FirebaseAuth.getInstance().currentUser


    }
}
