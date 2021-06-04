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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dref: DatabaseReference
private var firebaseuserID:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

       val toolbar:Toolbar=findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        mAuth=FirebaseAuth.getInstance()

            btn_register.setOnClickListener {

                registeruser()


            }
        already.setOnClickListener {
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
            finish()

        }









    }

    private fun registeruser() {
        val progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Register")
        progressDialog.setMessage("Please Wait......")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()


        val username=username_register.text.toString()
        val email=email_register.text.toString()
        val password=password_register.text.toString()

        if (username.isEmpty()) {
            username_register.error = "Enter Username"
            username_register.requestFocus()
            return
        }
        if (email.isEmpty()){
           email_register.error="Enter Email"
            email_register.requestFocus()
            return

        }
        if (password.isEmpty()) {
            password_register.error = "Enter Password"
            password_register.requestFocus()
            return
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {

                firebaseuserID=mAuth.currentUser?.uid.toString()
                dref=FirebaseDatabase.getInstance().reference.child("Users")
                    .child(firebaseuserID!!)


                val userhashmap=HashMap<String,Any>()
                userhashmap["uid"]=firebaseuserID.toString()
                userhashmap["profile"]="https://firebasestorage.googleapis.com/v0/b/whatsapp-1b7d3.appspot.com/o/profile.png?alt=media&token=ed055d47-44e8-422b-9355-d9af26008947"
                userhashmap["cover"]="https://firebasestorage.googleapis.com/v0/b/whatsapp-1b7d3.appspot.com/o/pexels-photo-670061.jpeg?alt=media&token=b24bfad2-dc3f-4a34-9764-dbbaa4dc0768"
                userhashmap["username"]=username
                userhashmap["status"]="offline"
                userhashmap["search"]=username.toLowerCase()
                userhashmap["facebook"]="https://m.facebook.com"
                userhashmap["instagram"]="https://m.instagram.com"

                dref.updateChildren(userhashmap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                        {
                            val intent= Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }



            }
            else
            {
                progressDialog.dismiss()
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
            }

        }
    }
}
