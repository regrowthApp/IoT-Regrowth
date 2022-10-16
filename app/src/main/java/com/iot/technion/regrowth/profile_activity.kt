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
import com.iot.technion.regrowth.databinding.ProfileActivityBinding
import com.iot.technion.regrowth.model.ProfileModel
import kotlinx.android.synthetic.main.profile_activity.*
import java.util.*


class profile_activity : AppCompatActivity() {


    private lateinit var binding : ProfileActivityBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var logoImg :Uri
    private lateinit var farmImg : Uri
    private lateinit var dialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = AlertDialog.Builder(this)
            .setMessage("Saving profile...")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.backgroundImg.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)
        }

        binding.logoImg.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,2)
        }

        binding.btnSaveProfile.setOnClickListener{
            validateInput()
            val name = binding.farmName.text!!.toString()
            val phone_number = binding.phoneNumber.text!!.toString()
            uploadData(name,phone_number,"")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.data != null){
                if(requestCode == 1) {
                    farmImg = data.data!!
                    binding.backgroundImg.setImageURI(farmImg)
                }
                else{
                    logoImg = data.data!!
                    binding.logoImg.setImageURI(logoImg)
                }
            }
        }
    }

    private fun uploadData(name : String, phone : String, email: String) {
        val email_address = intent.getStringExtra("id")!!.toString()
        var reference = storage.reference.child("Profile Pictures/${email_address}/farm")
        reference.delete()
        reference.putFile(farmImg)
                .addOnSuccessListener {
                    reference = storage.reference.child("Profile Pictures/${email_address}/logo")
                    reference.delete()
                    val farmImg = it.toString()
                    reference.putFile(logoImg)
                        .addOnSuccessListener {
                            val profile = ProfileModel(name, farmImg, it.toString(), phone, email)
                            database.reference.child("users/${email_address}/profile")
                                .setValue(profile)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@profile_activity,
                                        "updating profile done!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent: Intent =
                                        Intent(this@profile_activity, MainActivity::class.java)
                                    intent.putExtra("id", email_address)
                                    startActivity(intent)
                                }
                        }
                }
    }

    private fun validateInput(){
        if(binding.farmName.text!!.isEmpty()){
            Toast.makeText(this@profile_activity,"Please enter your name",Toast.LENGTH_SHORT).show()
        }
        else if(binding.phoneNumber.text!!.isEmpty()){
            Toast.makeText(this@profile_activity,"Please enter your phone number",Toast.LENGTH_SHORT).show()
        }
        else if(farmImg == null || logoImg == null){
            Toast.makeText(this@profile_activity,"Please select your image",Toast.LENGTH_SHORT).show()
        }
    }
}