package com.example.warmhearts.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.warmhearts.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingsActivity : AppCompatActivity() {
    private lateinit var personName: TextInputLayout
    private lateinit var userName: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var telephone: TextInputLayout
    private lateinit var saveButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        firebaseAuth = FirebaseAuth.getInstance()

        personName = findViewById(R.id.textInputName)
        userName = findViewById(R.id.textInputUserName)
        email = findViewById(R.id.textinputEMail)
        telephone = findViewById(R.id.textInputPhoneNumber)
        saveButton = findViewById(R.id.btn_save_settings)

        email.editText?.isFocusable = false
        email.editText?.setText(firebaseAuth.currentUser?.email.toString())

        val progressDialog = AlertDialog.Builder(this)
            .setTitle("Bir Saniye...")
            .setMessage("Lütfen Bekleyin...")
            .setCancelable(false)
            .show()

        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("AccountInfo")
        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.child("Name").value
                p0.child("User-Name").value
                p0.child("Telephone").value

                if(p0.child("Name").value != null){
                    personName.editText?.setText(p0.child("Name").value.toString())
                }
                if(p0.child("User-Name").value != null){
                    userName.editText?.setText(p0.child("User-Name").value.toString())
                }
                if(p0.child("Telephone").value != null){
                    telephone.editText?.setText(p0.child("Telephone").value.toString())
                }
                progressDialog.dismiss()
            }

        })

        saveButton.setOnClickListener {
            val nameText = personName.editText?.text.toString()
            val userNameText = userName.editText?.text.toString()
            val telephoneText = telephone.editText?.text.toString()

            databaseReference.child("Name").setValue(nameText)
            databaseReference.child("User-Name").setValue(userNameText)
            databaseReference.child("Telephone").setValue(telephoneText)
            Toast.makeText(this, "Kaydedildi", Toast.LENGTH_SHORT).show()
        }
    }
}
