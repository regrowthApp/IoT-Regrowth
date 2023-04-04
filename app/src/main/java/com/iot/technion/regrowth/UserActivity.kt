package com.iot.technion.regrowth

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.iot.technion.regrowth.databinding.ActivityUserBinding
import com.iot.technion.regrowth.model.ProfileModel
import java.io.File
import kotlin.math.log


class UserActivity : AppCompatActivity() {


    private lateinit var binding: ActivityUserBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var logoImg: Uri
    private lateinit var farmImg: Uri
    private lateinit var dialog: AlertDialog.Builder

    var logoImgSet: Boolean = false
    var farmImgSet: Boolean = false

    var uid: String = ""
    var intent_type: String = ""
    val TAG = "UserActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("id")!!.toString()
        uid.replace(".", "-")
        intent_type = intent.getStringExtra("type")!!.toString()

        dialog = AlertDialog.Builder(this)
            .setMessage("Saving profile...")
            .setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()


        if (intent_type.equals("edit")) {
            fillFromFirebase()
        } else {
            binding.cancel.text = "Do it Later"
        }

        binding.backgroundImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.logoImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }

        binding.saveProfileBtn.setOnClickListener {
            val name = binding.farmName.text!!.toString()
            val phone_number = binding.phoneNumber.text!!.toString()
            val email = binding.emailAddress.text!!.toString()
            val address = binding.address.text!!.toString()
            uploadData(name, phone_number, email, address)
        }

        binding.cancel.setOnClickListener {
            val intent = Intent(this@UserActivity, MainActivity::class.java)
            intent.putExtra("id", uid)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                if (requestCode == 1) {
                    farmImg = data.data!!
                    binding.backgroundImg.setImageURI(farmImg)
                    farmImgSet = true
                } else {
                    logoImg = data.data!!
                    binding.logoImg.setImageURI(logoImg)
                    logoImgSet = true
                }
            }
        }
    }

    private fun uploadData(name: String, phone: String, email: String, address: String) {
        var reference = storage.reference.child("Profile Pictures/${uid}")
        var farmImgPath = ""
        var logoImgPath = ""

        var settingFarmImage = true
        var settingLogoImage = true

        if (farmImgSet) {
            val farmRef = reference.child("farm")
            farmRef.delete()
            farmRef.putFile(farmImg).addOnSuccessListener {
                Log.e(TAG, "farm image uploaded")
                farmImgPath = "exist"
                settingFarmImage = false
            }.addOnFailureListener() {
                settingFarmImage = false
            }
        } else {
            farmImgPath = ""
            settingFarmImage = false
        }

        if (logoImgSet) {
            val logoRef = reference.child("logo")
            logoRef.delete()
            logoRef.putFile(logoImg).addOnSuccessListener {
                Log.e(TAG, "logo image uploaded")
                logoImgPath = "exist"
                settingLogoImage = false
            }.addOnFailureListener() {
                settingLogoImage = false
            }
        } else {
            logoImgPath = ""
            settingLogoImage = false
        }

        //keep checking in background if the images are uploaded
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (farmImgPath.equals("") || logoImgPath.equals("")) {
                    handler.postDelayed(this, 1000)
                } else {
                    saveProfileDataToFirebase(name, farmImgPath, logoImgPath, phone, email, address)
                }
            }
        }, 1000)
    }

    private fun saveProfileDataToFirebase(name: String, farmImgPath: String, logoImgPath: String, phone: String, email: String, address: String) {
        val profile = ProfileModel(name, farmImgPath, logoImgPath, phone, email, address)
        database.reference.child("users/${uid}/profile")
            .setValue(profile).addOnSuccessListener {
                Toast.makeText(
                    this@UserActivity,
                    "Profile Saved!",
                    Toast.LENGTH_LONG
                ).show()
                val intent: Intent = Intent(this@UserActivity, MainActivity::class.java)
                intent.putExtra("id", uid)
                startActivity(intent)
            }
    }

    private fun fillFromFirebase() {
        val storageRef = storage.reference.child("Profile Pictures")
        val ref = database.reference.child("users/${uid}/profile")
        ref.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var farm_name: String = ""
                    var emailAddress: String = ""
                    var phoneNumber: String = ""
                    var address: String = ""
                    if (dataSnapshot.hasChild("farm_name")) {
                        farm_name = dataSnapshot.child("farm_name").value!!.toString()
                    }

                    if (dataSnapshot.hasChild("email")) {
                        emailAddress = dataSnapshot.child("email").value.toString()
                    }

                    if (dataSnapshot.hasChild("phone_number")) {
                        phoneNumber = dataSnapshot.child("phone_number").value.toString()
                    }

                    if (dataSnapshot.hasChild("address")) {
                        address = dataSnapshot.child("address").value.toString()
                    }

                    if (dataSnapshot.child("farm_image").value.toString() != "") {
                        val farm_img = storageRef.child("${uid}/farm")
                        val local_farm = File.createTempFile("farmImg", "jpeg")
                        farm_img.getFile(local_farm).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_farm.absolutePath)
                            binding.backgroundImg.setImageBitmap(bitmap)
                        }
                    }

                    if (dataSnapshot.child("logo_image").value.toString() != "") {
                        val logo_img = storageRef.child("${uid}/logo")
                        val local_logo = File.createTempFile("logoImg", "jpeg")
                        logo_img.getFile(local_logo).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_logo.absolutePath)
                            binding.logoImg.setImageBitmap(bitmap)
                        }
                    }


                    binding.farmName.setText(farm_name)
                    binding.phoneNumber.setText(phoneNumber)
                    binding.emailAddress.setText(emailAddress)
                    binding.address.setText(address)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }
}