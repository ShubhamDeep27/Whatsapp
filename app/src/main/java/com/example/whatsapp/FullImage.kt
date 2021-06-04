package com.example.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_image.*

class FullImage : AppCompatActivity() {


    private var imageView:ImageView?=null
private var imageurl:String?=null
    private var senderid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

              senderid=intent.getStringExtra("senderid")




            val ref = FirebaseDatabase.getInstance().reference.child("Users")
                .child(senderid.toString())
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    val user = p0.getValue(Users::class.java)
                    val sendername = user?.getusername()
                    if (senderid==FirebaseAuth.getInstance().currentUser?.uid)
                    {
                        sender_id.text="You"
                    }

                    else {

                        sender_id.text = sendername
                    }
                }
            })


        val toolbar:Toolbar=findViewById(R.id.toolbar_full_image_viewer)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            finish()
        }

        imageurl=intent.getStringExtra("url")
      imageView=findViewById(R.id.image_viewer)
        Picasso.get().load(imageurl).into(imageView)


    }
}
