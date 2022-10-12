package com.iot.technion.regrowth

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iot.technion.regrowth.databinding.ActivityProfileBinding
import com.iot.technion.regrowth.model.ProfileModel
import java.util.*


class profile_activity : AppCompatActivity() {


    private lateinit var binding : ActivityProfileBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var selectedImg :Uri
    private lateinit var dialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this)
            .setMessage("Updating profile...")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.userImage.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }

        binding.btnEditProfile.setOnClickListener{
            validateInput()
            val name = binding.name.text!!.toString()
            val img = binding.userImage.toString()
            val birthday = binding.birthday.text!!.toString()
            val phone_number = binding.phoneNumber.text!!.toString()
            val email = binding.emailAddress.text!!.toString()
            uploadData(name,img,birthday,phone_number,email)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.data != null){
                selectedImg = data.data!!
                binding.userImage.setImageURI(selectedImg)
            }
        }
    }

    private fun uploadData(name : String, image : String, birthday : String, phone : String, email: String) {
        val email_address = intent.getStringExtra("id")!!.toString()
        val reference = storage.reference.child("Profile Pictures/${email_address}")
        reference.delete()
        reference.putFile(selectedImg)
                .addOnSuccessListener {
                    val profile = ProfileModel(name, it.toString(), birthday, phone, email)
                    database.reference.child("users/${email_address}/profile")
                        .setValue(profile)
                        .addOnSuccessListener {
                            Toast.makeText(this@profile_activity, "updating pprofile done!", Toast.LENGTH_LONG).show()
                            val intent : Intent= Intent(this@profile_activity, MainActivity::class.java)
                            intent.putExtra("id", email_address)
                            startActivity(intent)
                        }
                }
    }

    private fun validateInput(){
        if(binding.name.text!!.isEmpty()){
            Toast.makeText(this@profile_activity,"Please enter your name",Toast.LENGTH_SHORT).show()
        }
//        else if(binding.emailAddress.text!!.isEmpty()){
//            Toast.makeText(this@profile_activity,"Please enter your email",Toast.LENGTH_SHORT).show()
//        }
        else if(binding.phoneNumber.text!!.isEmpty()){
            Toast.makeText(this@profile_activity,"Please enter your phone number",Toast.LENGTH_SHORT).show()
        }
        else if(selectedImg == null){
            Toast.makeText(this@profile_activity,"Please select your image",Toast.LENGTH_SHORT).show( )
        }
    }
}