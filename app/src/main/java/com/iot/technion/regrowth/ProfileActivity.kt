package com.iot.technion.regrowth

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.iot.technion.regrowth.databinding.ActivityProfileBinding
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    val TAG = "ProfileActivityEdit"
    val database = FirebaseDatabase.getInstance()
    val storage = FirebaseStorage.getInstance().reference.child("Profile Pictures")
    private lateinit var uid: String
    var userDataIsReady = false
    var map: GoogleMap? = null
    var userAddress: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("id").toString()
        getFromFireBase()

        binding.backHome.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            intent.putExtra("id", uid)
            startActivity(intent)
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this@ProfileActivity, UserActivity::class.java)
            intent.putExtra("id", uid)
            intent.putExtra("type", "edit")
            startActivity(intent)
        }
        setUpMap()
        binding.qrButton.setOnClickListener {
            showQRCode()
        }

        if(intent.hasExtra("external")){
            binding.btnEditProfile.visibility = View.GONE
            binding.qrButton.visibility = View.GONE
        }

    }

    private fun showQRCode() {
        binding.qrCard.visibility = View.VISIBLE
        binding.closeQr.setOnClickListener {
            binding.qrCard.visibility = View.GONE
        }
        //Generate the QR image from uid
        val qrContent = "https://us-central1-regrowth-c498e.cloudfunctions.net/getProfile?id=$uid"
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 400, 400)
            binding.qrCode.setImageBitmap(bitmap)

            binding.shareQr.setOnClickListener {
                //share QR image

                val values = ContentValues()
                values.put(android.provider.MediaStore.Images.Media.TITLE, "Regrowth QR Code")
                values.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "QR Code for Regrowth")
                val uri = contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                try {
                    val out = contentResolver.openOutputStream(uri!!)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out!!.flush()
                    out.close()
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Failed to save image", e)
                }
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/jpeg"
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"))

            }
        } catch (e: Exception) {
            Log.e(TAG, "showQRCode: ${e.message}")
        }


    }

    private fun setUpMap() {

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            if (userDataIsReady) {
                convertAddressToLocation()
            }

        }
    }

    private fun convertAddressToLocation() {
        if (userAddress != null) {
            //zoom to an address
            val address:String = userAddress!!
            val geoCoder = Geocoder(this)
            val location = geoCoder.getFromLocationName(address, 1)
            if (location != null && location.size > 0) {
                val lat = location[0].latitude
                val lng = location[0].longitude
                val latLng = LatLng(lat, lng)
                map?.addMarker(MarkerOptions().position(latLng).title(address))
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            }
        }else{
            Log.e(TAG, "convertAddressToLocation: userAddress is null")
        }
    }

    private fun getFromFireBase() {
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
                        userAddress = address
                    }

                    if (dataSnapshot.child("farm_image").value.toString() != "") {
                        val farm_img = storage.child("${uid}/farm")
                        val local_farm = File.createTempFile("farmImg", "jpeg")
                        farm_img.getFile(local_farm).addOnSuccessListener {
                            Log.e(TAG, "onDataChange: ${it.totalByteCount}", )
                            val bitmap = BitmapFactory.decodeFile(local_farm.absolutePath)
                            binding.backgroundImg.setImageBitmap(bitmap)
                        }.addOnFailureListener(OnFailureListener {
                            Log.e(TAG, "onFailure: ${it.message}")
                        })
                    }else{
                        Log.e(TAG, "onDataChange: farm image is null")
                        Log.e(TAG, "onDataChange: dataSnapshot:${dataSnapshot.value}", )
                    }

                    if (dataSnapshot.child("logo_image").value.toString() != "") {
                        val logo_img = storage.child("${uid}/logo")
                        val local_logo = File.createTempFile("logoImg", "jpeg")
                        logo_img.getFile(local_logo).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(local_logo.absolutePath)
                            binding.logoImg.setImageBitmap(bitmap)
                        }
                    }else{
                        Log.e(TAG, "onDataChange: logo image is null")
                    }
                    binding.farmName.text = farm_name
                    binding.phoneNumber.text = phoneNumber
                    binding.emailAddress.text = emailAddress
                    binding.address.text = address
                    userDataIsReady = true
                    if(map != null){
                        convertAddressToLocation()
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