package com.iot.technion.regrowth.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iot.technion.regrowth.MainActivity
import com.iot.technion.regrowth.databinding.ItemNodeBinding
import com.iot.technion.regrowth.model.NodeModel

class GatewayAdapter(private val context: Context, private val animal_name : String, private val uid: String):
    RecyclerView.Adapter<GatewayAdapter.MyVH>() {
    val TAG = "GateWayPagerAdapter"
    var animal : String = animal_name
    val activity: MainActivity = context as MainActivity
    val ref = FirebaseDatabase.getInstance()

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: GatewayAdapter.MyVH, position: Int){
        var battery_sent = false
        var tension_sent = false
        ref.reference.child("users/$uid/$animal").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    if(dataSnapshot.child("gateway/nodeId").value.toString() != ""){
                        holder.binding.gateway.text = dataSnapshot.child("gateway/nodeId").value.toString()

                        if(dataSnapshot.child("gateway/battery").value.toString() != ""){
                            holder.binding.batteryPercentage.text = dataSnapshot.child("gateway/battery").value.toString()
                            battery_sent = true
                        }

                        if(dataSnapshot.child("gateway/tension").value.toString() != ""){
                            holder.binding.batteryTension.text = dataSnapshot.child("gateway/tension").value.toString()
                            tension_sent = true
                        }

                        if(tension_sent and battery_sent){
                            holder.binding.nodeConnection.text = "Connected"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            }
        )
    }

    class MyVH(view: ItemNodeBinding) : RecyclerView.ViewHolder(view.root) {
        val binding: ItemNodeBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GatewayAdapter.MyVH {
        val view = ItemNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }
}