package com.example.whatsapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val mContext: Context,private val mUserList:List<Users>,private val isChatCheck:Boolean):RecyclerView.Adapter<UserAdapter.ViewHolder>() {

   var laststring:String?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.user_search_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user=mUserList[position]

holder.itemView.setOnClickListener {

    val intent=Intent(mContext,MessageChat::class.java)
    intent.putExtra("profileID",user.getuid())
    mContext.startActivity(intent)
}

        holder.username.text=user.getusername()
        Picasso.get().load(user.getprofile()).placeholder(R.drawable.ic_profile).into(holder.profileimageview)


        if (isChatCheck)
        {
            retrivelastmessage(user.getuid(),holder.lastmessage)
        }
        else
        {
            holder.lastmessage.visibility=View.GONE
        }
        if (isChatCheck)
        {
            if (user.getstatus()=="online")
            {
                holder.onlinetxt.visibility=View.VISIBLE
                holder.offlinetxt.visibility=View.GONE
            }
            else
            {
                holder.offlinetxt.visibility=View.VISIBLE
                holder.onlinetxt.visibility=View.GONE
            }

        }
        else
        {

            holder.offlinetxt.visibility=View.GONE
            holder.onlinetxt.visibility=View.GONE

        }
    }

    private fun retrivelastmessage(chatuserid: String, lastmessage: TextView) {
        laststring="defaultMsg"
        val firebaseUsers=FirebaseAuth.getInstance().currentUser
        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {

                for (datasnapshot in p0.children)
                {
                    val chat=datasnapshot.getValue(Chat::class.java)
                    if (firebaseUsers!=null && chat!=null)
                    {
                        if (chat.getreciver()==firebaseUsers?.uid && chat.getsender()==chatuserid ||chat.getsender()==firebaseUsers?.uid && chat.getreciver()==chatuserid)
                        {

                            laststring=datasnapshot.child("message").getValue().toString()
                        }
                    }


                }


                when(laststring)
                {

                    "defaultMsg" ->lastmessage.text="No message"
                    "sent you an image" ->lastmessage.text="image sent"
                    else ->lastmessage.text=laststring
                }
                laststring="defaultMsg"
            }
        })




    }


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var username:TextView
        var profileimageview:CircleImageView
        var onlinetxt:CircleImageView
        var offlinetxt:CircleImageView
        var lastmessage:TextView



        init {
            username=itemView.findViewById(R.id.username_search)
            profileimageview=itemView.findViewById(R.id.profile_image_serach)
            onlinetxt=itemView.findViewById(R.id.image_online)
            offlinetxt=itemView.findViewById(R.id.image_offline)
            lastmessage=itemView.findViewById(R.id.message_last)
        }


    }



}