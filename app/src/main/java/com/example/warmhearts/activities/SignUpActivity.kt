package com.example.warmhearts.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.warmhearts.R
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var signUpButton: Button
    private lateinit var eMail: EditText
    private lateinit var passwordOne: EditText
    private lateinit var passwordTwo: EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        eMail = findViewById(R.id.editText_email_signUp)
        passwordOne = findViewById(R.id.editText_passwordOne_signUp)
        passwordTwo = findViewById(R.id.editText_passwordTwo_signUp)
        signUpButton = findViewById(R.id.btn_sign_up_signUp)
        firebaseAuth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_sign_up_signUp ->performSignUp()
        }
    }

    private fun performSignUp(){
        if(passwordOne.text.toString() != "" && passwordTwo.text.toString() != "" && eMail.text.toString() != ""){
            if(passwordOne.text.toString() == passwordTwo.text.toString()){
                // perform log in
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Kaydediyor...")
                    .setMessage("Lütfen Bekleyin...")
                    .setCancelable(false)
                    .show()

                val task: Task<AuthResult> = firebaseAuth.createUserWithEmailAndPassword(eMail.text.toString().toLowerCase(), passwordOne.text.toString())
                task.addOnCompleteListener {
                    alertDialog.dismiss()

                    if(task.isSuccessful){
                       finish()
                    }else{
                        Toast.makeText(this, "Bir hata oluştu: ${it.exception}", Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Snackbar.make(findViewById(R.id.constraint_sign_up), "Şifreler aynı değil", Snackbar.LENGTH_LONG).show()
            }
        }else {
            Snackbar.make(
                findViewById(R.id.constraint_sign_up),
                "Lütfen boş alanları doldurun",
                Snackbar.LENGTH_LONG
            ).show()
        }

    }
}
