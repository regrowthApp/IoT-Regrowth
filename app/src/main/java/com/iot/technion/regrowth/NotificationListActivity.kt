package com.iot.technion.regrowth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iot.technion.regrowth.adapters.NotificationAdapter
import com.iot.technion.regrowth.databinding.ActivityNotificationListBinding
import com.iot.technion.regrowth.model.NotificationModel

class NotificationListActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationListBinding
    lateinit var adapter: NotificationAdapter
     lateinit var notificationList: MutableList<NotificationModel>
     lateinit var userId:String
     var TAG = "NotificationListActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = intent.getStringExtra("id")!!
        notificationList = mutableListOf()
        adapter = NotificationAdapter(notificationList)
        binding.rvNotificationList.adapter = adapter
        binding.rvNotificationList.layoutManager = LinearLayoutManager(this)
        notificationListFromFirebase()
        supportActionBar?.setHomeButtonEnabled(true)

        binding.icCross.setOnClickListener {
            finish()
        }
    }

    private fun notificationListFromFirebase() {
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ref = database.getReference("notifications/users/$userId/")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (child in snapshot.children) {
                    val notification = child.getValue(NotificationModel::class.java)
                    notificationList.add(notification!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }
        })
    }


}