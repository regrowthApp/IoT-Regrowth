package com.iot.technion.regrowth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class OpenProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_profile)
        getProfileId()
    }

    private fun getProfileId() {
        //get profile id from url
        val profileId = intent.data
        Log.e("OpenProfileActivity", "getProfileId: $profileId")
        if (profileId.toString().contains("id")) {
            var id = profileId.toString().substringAfter("id=")
            Log.e("OpenProfileActivity", "getProfileId: $id")
            var intent: Intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("external", true)
            startActivity(intent)
        }
    }
}