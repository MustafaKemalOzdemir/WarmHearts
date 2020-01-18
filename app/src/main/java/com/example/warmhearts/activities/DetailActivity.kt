package com.example.warmhearts.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.warmhearts.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var headImage: ImageView
    private lateinit var header: TextView
    private lateinit var description: TextView
    private lateinit var age: TextView
    private lateinit var phoneButton: TextView
    private lateinit var userName: TextView
    private lateinit var location: TextView
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var fireBaseDataBase: FirebaseDatabase
    private lateinit var deletePost: Button
    private val MY_PERMISSIONS_REQUEST_CALL_PHONE = 552

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var checker = 0;
        setContentView(R.layout.activity_detail)
        val activity = this as DetailActivity

        headImage = findViewById(R.id.imageView2)
        header = findViewById(R.id.textView_header)
        description = findViewById(R.id.textView_description)
        age = findViewById(R.id.textView_age)
        phoneButton = findViewById(R.id.button_phone)
        userName = findViewById(R.id.textView_userName)
        location = findViewById(R.id.textView_location)
        deletePost = findViewById(R.id.buttonSil)
        fireBaseAuth = FirebaseAuth.getInstance()
        fireBaseDataBase = FirebaseDatabase.getInstance()

        phoneButton.setOnClickListener{
            val myIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "${phoneButton.text}"))

            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE)
            } else {
                try {
                    startActivity(myIntent)
                } catch(e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }



        val progressDialog = AlertDialog.Builder(this)
            .setTitle("Bir Saniye...")
            .setMessage("Lütfen Bekleyin...")
            .setCancelable(false)
            .show()

        val intent = intent
        intent.extras?.let {
            val owner:String = it.getString("owner", "")
            val postId:String = it.getString("post_id", "")
            buttonSil.setOnClickListener {
                fireBaseDataBase.reference.child("Users").child(owner).child("Posts").child(postId).removeValue()
                fireBaseDataBase.reference.child("Posts").child(postId).removeValue()
                activity.finish()
            }
            fireBaseDataBase.reference.child("Users").child(owner).child("AccountInfo").addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.v("testTest", "owner $owner, postid $postId")
                    if(owner == fireBaseAuth.currentUser?.uid){
                        buttonSil.visibility = View.VISIBLE
                    }
                    phoneButton.text = p0.child("Telephone").value.toString()
                    userName.text = p0.child("User-Name").value.toString()
                    checker++
                    if(checker == 2){
                        progressDialog.dismiss()
                    }
                }

            })
            fireBaseDataBase.reference.child("Users").child(owner).child("Posts").child(postId).addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    description.text = p0.child("Description").value.toString()
                    location.text = p0.child("Address").value.toString()
                    header.text = p0.child("Header").value.toString()
                    age.text = "Yaş: ${p0.child("Age").value.toString()}"
                    Picasso.get()
                        .load(p0.child("Image").value.toString())
                        .resize(324,0)
                        .centerInside()
                        .into(headImage)
                    checker++
                    if(checker == 2){
                        progressDialog.dismiss()
                    }
                }
            })

        }



    }
}
