package com.example.whatsapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChat : AppCompatActivity() {
    var userid:String?=null
    var firebaseUser:FirebaseUser?=null
    val requestcode=1
    var chatAdapter:ChatAdapter?=null
    var mChat:List<Chat>?=null
    lateinit var recyclerView:RecyclerView
    var refrence:DatabaseReference?=null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_messagechats)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent=Intent(this,Verifyphone::class.java)
            startActivity(intent)
            finish()
        }



        intent=intent
        userid=intent.getStringExtra("profileID")

        firebaseUser=FirebaseAuth.getInstance().currentUser
        recyclerView=findViewById(R.id.recyclerview_chat)
        recyclerView.setHasFixedSize(true)
        var LinearLayoutManager=LinearLayoutManager(applicationContext)
        LinearLayoutManager.stackFromEnd
        recyclerView.layoutManager=LinearLayoutManager



        refrence=FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userid.toString())

        refrence!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(Users::class.java)
                username_messagechat.text=user?.getusername()
                Picasso.get().load(user?.getprofile()).into(profile_image_messagechat)
                retrivemessages(firebaseUser?.uid,userid,user?.getprofile())





                 }
        })


        send_message_btn.setOnClickListener {

            val mgtext=text_message.text.toString()
            if (mgtext=="")
            {

            }
            else
            {
                sendmessagetouser(firebaseUser?.uid,userid,mgtext)

            }
            text_message.setText("")
            recyclerView.smoothScrollToPosition(chatAdapter!!.itemCount)
        }

attch_imagefile.setOnClickListener {
    val intent= Intent()
    intent.action=Intent.ACTION_GET_CONTENT
    intent.type="image/*"
  startActivityForResult(Intent.createChooser(intent,"Pick Image"),requestcode)


}
        seenmessage(userid.toString())

    }

    private fun retrivemessages(senderid: String?, reciverid: String?, reciverprofile: String?) {

        mChat=ArrayList()
        val refrence=FirebaseDatabase.getInstance().reference.child("Chats")
        refrence.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mChat as ArrayList).clear()
                for (snapshot in p0.children)
                {
                    val chat=snapshot.getValue(Chat::class.java)
                   val sender=snapshot.child("sender").getValue().toString()
                    val reciver=snapshot.child("reciver").getValue().toString()
                    val message=snapshot.child("message").getValue().toString()
                    val url=snapshot.child("url").getValue().toString()
                    val messageId=snapshot.child("messageId").getValue().toString()
                    val isseen=snapshot.child("isseen").getValue()


                    if (reciver.equals(senderid)&& sender.equals(reciverid)||reciver.equals(reciverid) && sender.equals(senderid))
                    {
                        (mChat as ArrayList).add(Chat(sender,reciver,message,
                            isseen as Boolean,messageId,url))
                    }
                    chatAdapter= ChatAdapter(this@MessageChat, mChat as ArrayList<Chat>,reciverprofile.toString())
                    recyclerView.adapter=chatAdapter

                }
                chatAdapter?.itemCount?.let { recyclerView.smoothScrollToPosition(it) }
            }


        })




    }

    private fun sendmessagetouser(senderid: String?, reciverid: String?, mgtext: String) {

        val dref=FirebaseDatabase.getInstance().reference
        val messagekey=dref.push().key


        val messagemap=HashMap<String,Any>()

        messagemap["sender"]=senderid.toString()
        messagemap["reciver"]=reciverid.toString()
        messagemap["message"]=mgtext
        messagemap["isseen"]=false
        messagemap["messageId"]=messagekey.toString()
        messagemap["url"]=""


        dref.child("Chats").child(messagekey.toString()).setValue(messagemap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val chatlistref=FirebaseDatabase.getInstance().reference
                        .child("ChatList").child(firebaseUser?.uid.toString())
                        .child(userid.toString())
                    chatlistref.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                           }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists())
                            {
                                chatlistref.child("id").setValue(userid)

                            }
                            val chatlistreciverref=FirebaseDatabase.getInstance().reference
                                .child("ChatList").child(userid.toString())
                                .child(firebaseUser?.uid.toString())

                            chatlistreciverref.child("id").setValue(firebaseUser?.uid.toString())

                        }
                    })



                    val refrence=FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser?.uid.toString())

                    ////push notification




                }
            }











    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==requestcode && resultCode==Activity.RESULT_OK &&data!=null && data?.data!=null)
        {
            val progressDialog= ProgressDialog(this)
            progressDialog.setTitle("Loading")
            progressDialog.setMessage("Please Wait......")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()


            val fileurl=data.data
            val storageReference=FirebaseStorage.getInstance().reference
                .child("Chat Images")
            val ref=FirebaseDatabase.getInstance().reference
            val messageId=ref.push().key

            val filepath=storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask= filepath!!.putFile(fileurl!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                if (!task.isSuccessful)
                {
                    task.exception.let {
                        throw it!!
                        progressDialog.dismiss()
                    }

                }

                return@Continuation filepath.downloadUrl
            }).addOnCompleteListener {
             task ->
                if (task.isSuccessful)
                {
                    val downloadurl=task.result
                    val url=downloadurl.toString()

                    val messagemap=HashMap<String,Any>()

                    messagemap["sender"]=firebaseUser?.uid.toString()
                    messagemap["reciver"]=userid.toString()
                    messagemap["message"]="sent you an image"
                    messagemap["isseen"]=false
                    messagemap["messageId"]=messageId.toString()
                    messagemap["url"]=url

                    ref.child("Chats").child(messageId.toString()).setValue(messagemap)
                    progressDialog.dismiss()
                }
            }

        }
    }
    var seenListener:ValueEventListener?=null
    private fun seenmessage(userId:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener=ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               for (snapshot in p0.children)
               {
                   val chat=snapshot.getValue(Chat::class.java)
                   if (chat?.getreciver().equals(firebaseUser?.uid)&& chat?.getsender().equals(userId))
                   {
                       Log.d("pig",seenListener.toString())
                       val hashmap=HashMap<String,Any>()
                       hashmap["isseen"]=true
                       snapshot.ref.updateChildren(hashmap)
                   }
               }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        refrence?.removeEventListener(seenListener!!)
    }
}
