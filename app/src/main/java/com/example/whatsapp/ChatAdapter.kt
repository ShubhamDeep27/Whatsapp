package com.example.whatsapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private var mContext: Context,private var mChat:List<Chat>,private var imageurl:String):RecyclerView.Adapter<ChatAdapter.viewHolder>() {
    var firebaseUser:FirebaseUser= FirebaseAuth.getInstance().currentUser!!


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): viewHolder {
        return if (position==1)
        {
            val view=LayoutInflater.from(mContext).inflate(R.layout.message_right_item,parent,false)
           viewHolder(view)
        }
        else
        {
            val view=LayoutInflater.from(mContext).inflate(R.layout.message_item_left,parent,false)
           viewHolder(view)
        }
    }

    override fun getItemCount(): Int {
      return mChat.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        val chat=mChat[position]



        //Images message
        if (chat.getmessage().equals("sent you an image") && !chat.geturl().equals("")) {

            //image message right side
            if (chat.getsender().equals(firebaseUser?.uid)) {
                holder.showtextmessage?.visibility = View.GONE
                holder.rightimageview?.visibility = View.VISIBLE
                Picasso.get().load(chat.geturl()).into(holder.rightimageview)
                holder.rightimageview?.setOnClickListener {
                    val option= arrayOf<CharSequence>(
                        "View full image"
                    ,"Delete Meassage"
                    ,"Cancel"
                    )


                   var builder:AlertDialog.Builder=AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Select")
                    builder.setItems(option,DialogInterface.OnClickListener { dialog, which ->
                        if (which==0)
                        {
                            val intent= Intent(mContext,FullImage::class.java)
                            intent.putExtra("url",chat.geturl())
                            intent.putExtra("senderid",chat.getsender())
                            mContext.startActivity(intent)

                        }
                      else if(which==1)
                        {
                             deletesentmessage(position,holder)
                        }
                       else
                        {
                            dialog.cancel()
                        }

                    })
                    builder.show()
                }

            }
            //image left side
            else if (!chat.getsender().equals(firebaseUser?.uid)) {
                holder.showtextmessage?.visibility = View.GONE
                holder.leftimageview?.visibility = View.VISIBLE
                Picasso.get().load(chat.geturl()).into(holder.leftimageview)
                holder.leftimageview?.setOnClickListener {
                    val option= arrayOf<CharSequence>(
                        "View full image"
                        ,"Cancel"
                    )


                    var builder:AlertDialog.Builder=AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Select")
                    builder.setItems(option,DialogInterface.OnClickListener { dialog, which ->
                        if (which==0)
                        {
                            val intent= Intent(mContext,FullImage::class.java)
                            intent.putExtra("url",chat.geturl())
                            intent.putExtra("senderid",chat.getsender())
                            mContext.startActivity(intent)

                        }
                        else
                        {
                            dialog.cancel()
                        }
                                           })
                    builder.show()
                }



            }
        }
        //textmessages
        else
        {
             if (firebaseUser?.uid==chat.getsender()) {
                 holder.showtextmessage?.text = chat.getmessage()
                 holder.showtextmessage?.setOnClickListener {
                     val option = arrayOf<CharSequence>(
                         "Delete Meassage"
                         , "Cancel"
                     )


                     var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                     builder.setTitle("Select")
                     builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
                         if (which == 0) {
                             deletesentmessage(position, holder)
                         } else {
                             dialog.cancel()
                         }


                     })
                     builder.show()
                 }
             }

        }
        if (position==mChat.size-1)
        {
            if (chat.getisseen()) {
                holder.textseen?.text = "Seen"
            }
            else
            {
                holder.textseen?.text="Sent"
            }

        }
        else
        {
            holder.textseen?.visibility=View.GONE
        }
        Picasso.get().load(imageurl).into(holder.profileimage)

    }


    inner class viewHolder(itemview:View):RecyclerView.ViewHolder(itemview)
    {
        var profileimage:CircleImageView?=null
        var showtextmessage:TextView?=null
        var leftimageview:ImageView?=null
        var textseen:TextView?=null
        var rightimageview:ImageView?=null



        init {
            profileimage=itemview.findViewById(R.id.profile_image_chat)
            showtextmessage=itemview.findViewById(R.id.show_text_message)
            leftimageview=itemview.findViewById(R.id.left_image_view)
            rightimageview=itemview.findViewById(R.id.right_image_view)
            textseen=itemview.findViewById(R.id.text_seen)

        }
    }

    override fun getItemViewType(position: Int): Int {



        return if (mChat[position].getsender().equals(firebaseUser?.uid))
        {
            1
        }
        else
        {
            0
        }
    }
    private fun deletesentmessage(position: Int,holder: viewHolder)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChat.get(position).getmessageId()!!)
            .removeValue()





    }
}