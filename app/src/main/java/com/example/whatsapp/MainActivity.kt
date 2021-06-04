package com.example.whatsapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.whatsapp.fragment.Chats
import com.example.whatsapp.fragment.Search
import com.example.whatsapp.fragment.Settings
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var databaseReference:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

         val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById(R.id.view_pager)

        val viewPagerAdapter=viewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addfragment(Chats(),"Chats")
        viewPagerAdapter.addfragment(Search(),"Search")
        viewPagerAdapter.addfragment(Settings(),"Settings")
        viewPager.adapter=viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)


        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                val viewPagerAdapter=viewPagerAdapter(supportFragmentManager)
                var countunreadmessage=0
                for (snapshot in p0.children)
                {
                    val chat=snapshot.getValue(Chat::class.java)
                    if (chat?.getreciver().equals(firebaseUser?.uid)&& !chat!!.getisseen())
                    {
                        countunreadmessage += 1
                    }
                }
                if (countunreadmessage==0)
                {

                    viewPagerAdapter.addfragment(Chats(),"Chats")
                }
                else
                {
                    viewPagerAdapter.addfragment(Chats(),"Chats($countunreadmessage)")
                }

                viewPagerAdapter.addfragment(Search(),"Search")
                viewPagerAdapter.addfragment(Settings(),"Settings")
                viewPager.adapter=viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)

                }
        })




            display()




    }

    private fun display() {

        firebaseUser=FirebaseAuth.getInstance().currentUser
        databaseReference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser?.uid.toString())


        databaseReference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user= p0.getValue(Users::class.java)!!
                    user_name.text=user.getusername()
                    Picasso.get().load(user.getprofile()).placeholder(R.drawable.ic_profile).into(profile_image)

                }
            }


        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }


    internal class viewPagerAdapter(fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager)
    {
        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>


        init {
            fragments= ArrayList<Fragment>()
            titles= ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
           return fragments.size
        }

        fun addfragment(fragment: Fragment,title:String)
        {
            fragments.add(fragment)
            titles.add(title)

        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }



    }
    private fun updatestatus(status:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser?.uid.toString())

val hashmap=HashMap<String,Any>()
        hashmap["status"]=status
        ref.updateChildren(hashmap)
            }


    override fun onResume() {
        super.onResume()


        updatestatus("online")
    }


    override fun onPause() {
        super.onPause()

        updatestatus("offline")
    }
      }
