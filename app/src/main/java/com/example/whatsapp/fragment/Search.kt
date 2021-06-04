package com.example.whatsapp.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.whatsapp.R
import com.example.whatsapp.UserAdapter
import com.example.whatsapp.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class Search : Fragment() {
    private var userAdapter:UserAdapter?=null
    private var mUser:List<Users>?=null
 private var recyclerView:RecyclerView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_search, container, false)



        recyclerView=view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)

        mUser=ArrayList()

         view.search_text.addTextChangedListener(object :TextWatcher{
             override fun afterTextChanged(s: Editable?) {

             }

             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

             }

             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                 if (search_text.text.toString()=="")
                 {

                 }
                 else {
                          retriveallusers()
                     searchuser(s.toString().toLowerCase())
                 }



             }
         })



        return view
    }

    private fun retriveallusers() {
        var firebaseID=FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference= FirebaseDatabase.getInstance().reference.child("Users")

        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUser as ArrayList<Users>).clear()


                if (search_text.text.isEmpty()) {
                    for (snapshot in p0.children) {

                        val user = snapshot.getValue(Users::class.java)
                        val username = snapshot.child("username").getValue().toString()
                        val uid = snapshot.child("uid").getValue().toString()
                        val profile = snapshot.child("profile").getValue().toString()
                        val cover = snapshot.child("cover").getValue().toString()
                        val status = snapshot.child("status").getValue().toString()
                        val search = snapshot.child("search").getValue().toString()
                        val facebook = snapshot.child("facebook").getValue().toString()
                        val instagram = snapshot.child("instagram").getValue().toString()

                        if (user != null && uid != firebaseID) {

                            (mUser as ArrayList<Users>).add(
                                Users(
                                    uid,
                                    profile,
                                    cover,
                                    username,
                                    status,
                                    search,
                                    facebook,
                                    instagram
                                )
                            )
                            Log.d("shot", mUser.toString())
                        }


                    }
                    userAdapter = UserAdapter(context!!, mUser as ArrayList<Users>, false)
                    recyclerView?.adapter = userAdapter

                }
            }
        })





    }



    private fun searchuser(str:String){

        var firebaseID=FirebaseAuth.getInstance().currentUser?.uid
        val queryUser= FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")


        queryUser.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUser as ArrayList).clear()
                for (snapshot in p0.children)

                {

                    val user=snapshot.getValue(Users::class.java)
                    val username=snapshot.child("username").getValue().toString()
                    val uid=snapshot.child("uid").getValue().toString()
                    val profile=snapshot.child("profile").getValue().toString()
                    val cover=snapshot.child("cover").getValue().toString()
                    val status=snapshot.child("status").getValue().toString()
                    val search=snapshot.child("search").getValue().toString()
                    val facebook=snapshot.child("facebook").getValue().toString()
                    val instagram=snapshot.child("instagram").getValue().toString()

                    if (user!=null && uid!=firebaseID)
                    {

                        (mUser as ArrayList<Users>).add(Users(uid, profile, cover, username, status, search, facebook, instagram))
                        Log.d("shot",mUser.toString())
                    }




                }
                userAdapter= UserAdapter(context!!, mUser as ArrayList<Users>,false)
                recyclerView?.adapter=userAdapter
                userAdapter?.notifyDataSetChanged()


            }
        })


    }


}
