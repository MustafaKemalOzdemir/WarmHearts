package com.example.warmhearts.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.warmhearts.R
import com.example.warmhearts.SessionManager
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import java.util.*

class LogInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var eMail: EditText
    private lateinit var password: EditText
    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        fireBaseAuth = FirebaseAuth.getInstance()
        signInButton = findViewById(R.id.btn_login)
        signUpButton = findViewById(R.id.btn_sign_up)
        eMail = findViewById(R.id.editText_email_login)
        password = findViewById(R.id.editText_passwordLogin)
        signUpButton.setOnClickListener(this)
        signInButton.setOnClickListener(this)
        eMail.setOnClickListener(this)
        password.setOnClickListener(this)
        sessionManager = SessionManager(this)


        if (sessionManager.getEmail().toLowerCase(Locale.getDefault()) != "" &&
            sessionManager.getPassword() != ""
        ) {
            signIn(sessionManager.getEmail(), sessionManager.getPassword())
        }

    }


    private fun signIn(eMail: String, password: String) {
        val progressDialog = AlertDialog.Builder(this)
            .setTitle("Giriş Yapılıyor...")
            .setMessage("Lütfen Bekleyin...")
            .setCancelable(false)
            .show()
        val task: Task<AuthResult> = fireBaseAuth.signInWithEmailAndPassword(eMail, password)
        task.addOnCompleteListener {
            progressDialog.dismiss()
            if (task.isSuccessful) {
                sessionManager.setLoginData(eMail, password)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                try {
                    task.exception?.let {
                       throw it
                    }
                } catch (e: FirebaseAuthWeakPasswordException) {
                    Toast.makeText(
                        this,
                        "Password is not strong enough",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        this,
                        "Invalid email address",
                        Toast.LENGTH_SHORT
                    ).show();
                } catch (e: FirebaseAuthUserCollisionException) {
                    Toast.makeText(
                        this,
                        "User is already registered",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Log.v("firebase eror", e.toString());
                    Toast.makeText(
                        this,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show();
                }
                Toast.makeText(this, "Email yada şifre hatalı!!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = fireBaseAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    override fun onClick(v: View?) {
        Log.v("LogIn", "clicked")
        when (v?.id) {
            R.id.btn_sign_up -> openSignUpActivity()
            R.id.btn_login -> startSigningIn()
        }
    }

    private fun openSignUpActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun startSigningIn() {
        if (eMail.text.toString() != "" && password.text.toString() != "") {
            signIn(eMail.text.toString(), password.text.toString())

        } else {
            Snackbar.make(
                findViewById(R.id.constraint_log_in),
                "Şifre yada kullanıcı adı alanları boş olamaz",
                Snackbar.LENGTH_LONG
            ).show()
        }

    }

}
