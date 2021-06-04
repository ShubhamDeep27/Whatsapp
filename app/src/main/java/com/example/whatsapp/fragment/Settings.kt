package com.example.whatsapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

import com.example.whatsapp.R
import com.example.whatsapp.Users
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class Settings : Fragment() {

   private var dref:DatabaseReference?=null
   private var firebaseUser:FirebaseUser?=null
    private val requestcode=32
    private var imageuri:Uri?=null
  private var storageref:StorageReference?=null
   private var coverchecker:String?=null
    private var socialchecker:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)


        firebaseUser=FirebaseAuth.getInstance().currentUser
        dref=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser?.uid.toString())
        storageref=FirebaseStorage.getInstance().reference.child("User Images")



        dref!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()) {
                   val user = p0.getValue(Users::class.java)
                   Log.d("pit1",user.toString())

                       view.username_settings.text = user?.getusername()
                       Picasso.get().load(user?.getprofile()).into(view.profile_image_settings)
                       Picasso.get().load(user?.getcover()).into(view.cover_image)

                   }
               }



        })
        view.profile_image_settings.setOnClickListener {
            pickimage()
        }
        view.cover_image.setOnClickListener{
            coverchecker="cover"
            pickimage()
        }
       view.set_facebook.setOnClickListener {

            socialchecker="facebook"
           setsociallink()
        }
        view.set_instagram.setOnClickListener {

            socialchecker="instagram"
            setsociallink()

        }





        return view

    }

    private fun setsociallink() {
      val builder:AlertDialog.Builder=
          AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)



            builder.setTitle("Write username")



        val editText=EditText(context)
        if (!socialchecker.isNullOrBlank())
        {
            editText.hint="username"
        }
       builder.setView(editText)

        builder.setPositiveButton("Save",DialogInterface.OnClickListener{

            dialog, which ->
            val str=editText.text.toString()
            if (str=="")
            {
                editText.error="Please enter Username"
                editText.requestFocus()
                    return@OnClickListener

            }
            else
            {
                savesociallink(str)
            }
        })

        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{

                dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun savesociallink(str: String) {
        val hashmapsocial=HashMap<String,Any>()
        when(socialchecker)
        {
            "facebook"->
            {
                hashmapsocial["facebook"]="https://m.facebook.com/$str"
            }
            "instagram"->
            {
                hashmapsocial["instagram"]="https://m.instagram.com/$str"
            }
        }
        dref?.updateChildren(hashmapsocial)?.addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {
                Toast.makeText(context,"Updated Sucessfully",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun pickimage() {
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,requestcode)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==requestcode && resultCode==Activity.RESULT_OK &&data?.data!=null)
        {
         imageuri=data.data
            uploadImage()


        }
    }

    private fun uploadImage() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("Please Wait...")
        progressBar.show()

        if(imageuri!=null)
        {
            val fileref=storageref
            ?.child(System.currentTimeMillis().toString()+".jpg")



            var uploadTask:StorageTask<*>
            uploadTask= fileref!!.putFile(imageuri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task ->

                if (!task.isSuccessful)
                {
                    task.exception.let {
                        throw it!!
                        progressBar.dismiss()
                    }

                }

                   return@Continuation fileref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadurl=task.result
                    val url=downloadurl.toString()
                    if (coverchecker=="cover")
                    {
                        val hashmap=HashMap<String,Any>()
                       hashmap["cover"] =url

                        dref?.updateChildren(hashmap)
                        coverchecker=""
                    }
                    else
                    {
                        val hashmapprofile=HashMap<String,Any>()
                        hashmapprofile["profile"] =url

                        dref?.updateChildren(hashmapprofile)
                        coverchecker=""

                    }
                    progressBar.dismiss()
                }
            }

        }









    }


}
