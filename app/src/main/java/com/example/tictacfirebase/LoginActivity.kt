package com.example.tictacfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null;
    private var auth: FirebaseAuth? = null;
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance();
    private var myRef  = db.reference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Auth service  init
        auth = FirebaseAuth.getInstance();
    }


    override fun onStart() {
        super.onStart()
        LoadMainActivity();
    }

    fun btLoginEvent(view: View) {
        // get login data
        var email = etLogin.text.toString();
        var pass = etPassword.text.toString();
        LoginToFirebase(email, pass);
    }

    fun LoginToFirebase(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    LoadMainActivity();
                } else {
                    Log.w("LoginUserWithEmail:failure", task.exception)
                }
            }
    }

    fun CreateAndLoginToFirebase(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    myRef.child("users").child(SplitString(auth!!.currentUser!!.email.toString())).child("Request").setValue(auth!!.currentUser!!.uid);
                    LoadMainActivity();
                } else {
                    Log.w("createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Failed to create", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun LoadMainActivity() {

        var currentUser = auth!!.currentUser;
        if (currentUser == null) {
            Toast.makeText(applicationContext, "User is not defined", Toast.LENGTH_LONG).show()
            return;
        }


        var intent = Intent(this, MainActivity::class.java);
        intent.putExtra("email", currentUser.email);
        intent.putExtra("uid", currentUser.uid);

        Toast.makeText(applicationContext, "Successful login with ${currentUser.email}", Toast.LENGTH_LONG).show();
        startActivity(intent);
        finish();
    }

    fun btCreateEvent(view: View) {
        var email = etLogin.text.toString();
        var pass = etPassword.text.toString();
        CreateAndLoginToFirebase(email, pass);
    }


    fun SplitString(str :String) : String {
        var result = str.split("@");
        return result[0];
    }
}