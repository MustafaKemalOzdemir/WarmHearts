package com.example.warmhearts.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warmhearts.MainRwAdapter
import com.example.warmhearts.R
import com.example.warmhearts.SessionManager
import com.example.warmhearts.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.floatingActionButtonAddAdvert

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDataBase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var postList = ArrayList<Post>()
        val context = this

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDataBase = FirebaseDatabase.getInstance()
        val postReference = firebaseDataBase.reference.child("Posts")

        val recyclerView = findViewById<RecyclerView>(R.id.rw_main)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        val adapter = MainRwAdapter(postList, object: CallBackInterface{
            override fun clicked(position: Int) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("owner", postList[position].owner)
                intent.putExtra("post_id", postList[position].postId)
                startActivity(intent)
            }

        })
        recyclerView.adapter = adapter

        postReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val childList = p0.children
                postList = ArrayList<Post>()
                for (child: DataSnapshot in childList){
                    val post = Post()
                    post.postId = child.key.toString()
                    post.owner = child.child("Owner").value.toString()
                    post.header = child.child("Header").value.toString()
                    post.photo = child.child("Image").value.toString()
                    post.description = child.child("Description").value.toString()
                    post.age = child.child("Age").value.toString()
                    post.location = child.child("Address").value.toString()
                    postList.add(post)
                }
                adapter.updateData(postList)
            }
        })

        floatingActionButtonAddAdvert.setOnClickListener {
            firebaseAuth.currentUser?.uid?.let {
                firebaseDataBase.reference.child("Users").child((it)).child("AccountInfo").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val userName = p0.child("User-Name").value.toString()
                        val name = p0.child("Name").value.toString()
                        val phone = p0.child("Telephone").value.toString()

                        if(userName != "null" && name !="null" && phone != "null"){
                            startActivity(Intent(context, AddAdvertActivity::class.java))
                        }else{
                            Toast.makeText(context, "Önce Ayarlar Bölümünden Eksik Bilgileri Doldurun!!", Toast.LENGTH_LONG).show()
                        }
                    }
                })

            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_log_out -> logOut()
        }
        return true
    }

    private fun logOut() {
        SessionManager(this).logout()
        startActivity(Intent(this,LogInActivity::class.java))
        finish()
    }

}
