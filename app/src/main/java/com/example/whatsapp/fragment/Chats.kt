package com.example.whatsapp.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Chats : Fragment() {
    private  var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var userschatlist:List<ChatList>?=null
    private lateinit var recyclerView: RecyclerView
    private var firebaseUser:FirebaseUser?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_chats, container, false)


        firebaseUser=FirebaseAuth.getInstance().currentUser
        recyclerView=view.findViewById(R.id.recyclerview_chatList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(context)


        userschatlist=ArrayList()


        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser?.uid.toString())
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                (userschatlist as ArrayList).clear()
                for (snapshot in p0.children)
                {
                    val chatlist=snapshot.getValue(Chat::class.java)
                    val id=snapshot.child("id").getValue().toString()
                    (userschatlist as ArrayList).add(ChatList(id))

                }
                retrivechatlist()
            }
        })



   return view
    }

    private fun retrivechatlist()

    {

       mUsers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList).clear()


                for (snapshot in p0.children)
                {
                    val user=snapshot.getValue(Users::class.java)

                    for (eachchatlist in userschatlist!!)
                    {

                        val username = snapshot.child("username").getValue().toString()
                        val uid = snapshot.child("uid").getValue().toString()
                        val profile = snapshot.child("profile").getValue().toString()
                        val cover = snapshot.child("cover").getValue().toString()
                        val status = snapshot.child("status").getValue().toString()
                        val search = snapshot.child("search").getValue().toString()
                        val facebook = snapshot.child("facebook").getValue().toString()
                        val instagram = snapshot.child("instagram").getValue().toString()

                        if (uid.equals(eachchatlist.getid()))
                        {


                        (mUsers as ArrayList).add(Users(uid, profile, cover, username, status, search, facebook, instagram))
                    }
                        }

                }
                userAdapter= UserAdapter(context!!, mUsers as ArrayList<Users>,true)
                recyclerView.adapter=userAdapter
            }
        })




    }
}
