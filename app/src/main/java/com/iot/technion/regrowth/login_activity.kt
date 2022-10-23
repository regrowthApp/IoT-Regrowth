package com.iot.technion.regrowth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.iot.technion.regrowth.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var database: DatabaseReference
    private lateinit var account: GoogleSignInAccount
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.loginbtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val user_id = email.substringBefore("@").replace(".","-")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { it ->
                    if (it.isSuccessful) {
                        FirebaseMessaging.getInstance().subscribeToTopic(user_id)
                        database.ref.child("users").get()
                            .addOnCompleteListener {
                                if (it.isSuccessful and it.result.hasChild(user_id)) {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.putExtra("id", user_id)
                                    startActivity(intent)
                                }
                            }
                    }
                    else{
                        FirebaseMessaging.getInstance().subscribeToTopic(user_id)
                        val intent = Intent(this@LoginActivity, UserActivity::class.java)
                        intent.putExtra("id", user_id)
                        intent.putExtra("type","create")
                        startActivity(intent)
                    }
                }
        }

        // Google auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("248185106763-ildj0j39c5t1ccvn19am6or45snkuvi5.apps.googleusercontent.com")
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        Firebase.database.setPersistenceEnabled(true)
        database = Firebase.database.reference

        binding.googleBtn.setOnClickListener {
            val loginIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(loginIntent, 1)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth = Firebase.auth
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Good auth", "signInWithCredential:success")
                            val user = auth.currentUser
                            val uid = user?.email!!.substringBefore("@").replace(".","-")
                            database.ref.child("users").get()
                                .addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        if (it.result.hasChild(uid)) {
                                            FirebaseMessaging.getInstance().subscribeToTopic(uid)
                                            val intent =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                            intent.putExtra("id", uid)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(this@LoginActivity, UserActivity::class.java)
                                            intent.putExtra("id", uid)
                                            intent.putExtra("type", "create")
                                            startActivity(intent)
                                        }
                                    }
                                }
                        } else {
                            //print(task.getException().toString())
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Auth Failed", Toast.LENGTH_LONG).show()

                        }
                    }

            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                //"Google sign in failed:("

            }

        }
    }
}