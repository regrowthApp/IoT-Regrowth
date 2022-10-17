package com.iot.technion.regrowth

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.iot.technion.regrowth.databinding.ActivityProfileBinding
import java.io.File

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val TAG = "ProfileActivityEdit"
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance().reference.child("Profile Pictures")
    private lateinit var uid : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("id").toString()
        getFromFireBase()

        binding.backHome.setOnClickListener{
            val intent = Intent(this@ProfileActivity,MainActivity::class.java)
            intent.putExtra("id",uid)
            startActivity(intent)
        }

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(this@ProfileActivity,UserActivity::class.java)
            intent.putExtra("id",uid)
            intent.putExtra("type","edit")
            startActivity(intent)
        }
    }

    private fun getFromFireBase(){
        val ref = database.reference.child("users/${uid}/profile")
            ref.addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    var farm_name : String = ""
                    var emailAddress : String = ""
                    var phoneNumber : String = ""
                    if(dataSnapshot.hasChild("farm_name")){
                        farm_name = dataSnapshot.child("farm_name").value!!.toString()
                    }

                    if(dataSnapshot.hasChild("email")){
                        emailAddress = dataSnapshot.child("email").value.toString()
                    }

                    if(dataSnapshot.hasChild("phone_number")){
                        phoneNumber = dataSnapshot.child("phone_number").value.toString()
                    }

                    if(dataSnapshot.child("farm_image").value.toString() != "") {
                        val farm_img = storage.child("${uid}/farm")
                        val local_farm = File.createTempFile("farmImg", "jpeg")
                        farm_img.getFile(local_farm).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_farm.absolutePath)
                            binding.backgroundImg.setImageBitmap(bitmap)
                        }
                    }

                    if(dataSnapshot.child("logo_image").value.toString() != "") {
                        val logo_img = storage.child("${uid}/logo")
                        val local_logo = File.createTempFile("logoImg", "jpeg")
                        logo_img.getFile(local_logo).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_logo.absolutePath)
                            binding.logoImg.setImageBitmap(bitmap)
                        }
                    }

                    binding.farmName.text = farm_name
                    binding.phoneNumber.text = phoneNumber
                    binding.emailAddress.text = emailAddress
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            }
        )


    }
}