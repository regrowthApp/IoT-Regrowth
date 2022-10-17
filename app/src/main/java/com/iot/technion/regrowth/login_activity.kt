package com.iot.technion.regrowth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.iot.technion.regrowth.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class data{
    var animal_weight = 0.0
    var animal_temp = 0.0
    var animal_humidity = 0.0
    var activity = 0

    constructor(weight : Double , temp: Double, humidity: Double, activity: Int){
        this.animal_weight = weight
        this.animal_temp = temp
        this.animal_humidity = humidity
        this.activity = activity
    }
}

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var binding: ActivityLoginBinding
    private lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.d(TAG, "google api available")
        } else {
            //Google Play Services are not available, or not updated
            Log.d(TAG, "google api not available!")
        }

        binding.googleBtn.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("248185106763-ildj0j39c5t1ccvn19am6or45snkuvi5.apps.googleusercontent.com")
                .requestEmail()
                .build()

            uid = gso.account.toString().substringBefore("@")
            checkifUserAlreadyRegistered(this,gso)
        }


        binding.loginbtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { it ->
                    if (it.isSuccessful) {
                        uid = email.substringBefore("@")
                        database.reference.child("users").get()
                            .addOnCompleteListener {
                                if (it.result.hasChild(uid)) {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.putExtra("id", uid)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this@LoginActivity, UserActivity::class.java)
                                    intent.putExtra("id", uid)
                                    intent.putExtra("type","create")
                                    startActivity(intent)
                                }
                            }
                    }
                    else{
                        Log.d("error","could not connect to firebase!!")
                    }
                }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, UserActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }

    private fun checkifUserAlreadyRegistered(activity: Activity, gso : GoogleSignInOptions){
        database.reference.child("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.hasChild("uid")){
                        val intent : Intent = Intent(this@LoginActivity,MainActivity::class.java)
                        intent.putExtra("id",uid)
                        startActivity(intent)
                    }
                    else{
                        googleSignInClient = GoogleSignIn.getClient(activity, gso)
                        val signInIntent = googleSignInClient.signInIntent
                        startActivityForResult(signInIntent, RC_SIGN_IN)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }
}