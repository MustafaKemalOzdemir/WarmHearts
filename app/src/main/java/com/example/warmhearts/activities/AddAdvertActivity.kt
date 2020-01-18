package com.example.warmhearts.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.warmhearts.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI

class AddAdvertActivity : AppCompatActivity() {
    private lateinit var fireBaseDatabase: FirebaseDatabase
    private lateinit var fireBaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private val fireBaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var image: ImageView
    private lateinit var description: TextInputLayout
    private lateinit var address: TextInputLayout
    private lateinit var age: TextInputLayout
    private lateinit var header: TextInputLayout
    private lateinit var postButton: Button
    private val PICK_IMAGE = 1
    private val mContext = this
    private var bitmap: Bitmap? = null
    private val path = "${fireBaseAuth.currentUser?.uid}_${System.currentTimeMillis()}"
    private var mUri: Uri?=null
    private var progressDialog: AlertDialog?=null

    val runnableShowOk = Runnable {
        progressDialog?.dismiss()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_advert)
        fireBaseDatabase = FirebaseDatabase.getInstance()
        fireBaseStorage = FirebaseStorage.getInstance()
        storageReference = fireBaseStorage.reference
        image = findViewById(R.id.imageView)
        description = findViewById(R.id.textInputDescription)
        address = findViewById(R.id.textInputLocation)
        age = findViewById(R.id.textInputAge)
        postButton = findViewById(R.id.buttonSaveAddAdvertActivity)
        header = findViewById(R.id.textInputHeader)

        val databaseUserPostReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Posts").child(path)
        val databasePostsReference =
            FirebaseDatabase.getInstance().reference.child("Posts").child(path)

        image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        postButton.setOnClickListener {
            bitmap?.let {
                progressDialog = AlertDialog.Builder(this)
                    .setTitle("Bir Saniye Paylaşılıyor...")
                    .setMessage("Lütfen Bekleyin...")
                    .setCancelable(false)
                    .show()
                val descriptionText = description.editText?.text.toString()
                val ageText = age.editText?.text.toString()
                val addressText = address.editText?.text.toString()
                val headerText = header.editText?.text.toString()

                if (descriptionText != "" && ageText != "" && addressText != "") {
                    val ref = storageReference.child("$path.jpg")
                    mUri?.let {myUri->
                        ref.putFile(myUri).addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {finalUri->
                                databaseUserPostReference.child("Image").setValue(finalUri.toString())
                                databaseUserPostReference.child("Description").setValue(descriptionText)
                                databaseUserPostReference.child("Age").setValue(ageText)
                                databaseUserPostReference.child("Address").setValue(addressText)
                                databaseUserPostReference.child("Header").setValue(headerText)

                                databasePostsReference.child("Image").setValue(finalUri.toString())
                                databasePostsReference.child("Owner").setValue(fireBaseAuth.currentUser?.uid)
                                databasePostsReference.child("Description").setValue(descriptionText)
                                databasePostsReference.child("Age").setValue(ageText)
                                databasePostsReference.child("Address").setValue(addressText)
                                databasePostsReference.child("Header").setValue(headerText)
                                Toast.makeText(mContext, "Işlem Tamam", Toast.LENGTH_SHORT).show()
                                Handler().postDelayed(runnableShowOk, 2000)
                            }
                        }
                    }
                }else{
                    Toast.makeText(mContext, "Boş Alanları Doldurun", Toast.LENGTH_SHORT).show()
                }

            } ?: run {
                Toast.makeText(mContext, "Önce Resim Yükleyin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            data?.data?.let {
                mUri = it
                val inputStream = this.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(inputStream)
                image.adjustViewBounds
                image.setImageBitmap(bitmap)
            } ?: run {
                Toast.makeText(mContext, "Hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
