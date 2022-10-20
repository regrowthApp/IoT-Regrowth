package com.iot.technion.regrowth.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.iot.technion.regrowth.MainActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iot.technion.regrowth.databinding.ItemNodeBinding
import com.iot.technion.regrowth.model.NodeModel


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class NodesAdapter(private val context: Context, private val animal_name : String, private val nodelist: MutableMap<String, NodeModel>,private val uid: String):
    RecyclerView.Adapter<NodesAdapter.MyVH>() {
    val TAG = "SectionsPagerAdapter"
    var nodes: MutableMap<String,NodeModel> = nodelist
    var animal : String = animal_name
    val activity:MainActivity = context as MainActivity
    val ref = FirebaseDatabase.getInstance()

    override fun getItemCount(): Int {
        return nodes.size
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        var node=nodes.toList()[position].second
        ref.reference.child("users/${uid}/${animal}").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("nodes/${node.gatewayId}")){
                        val tmp_ref = dataSnapshot.child("nodes/${node.gatewayId}")
                        if(tmp_ref.child("battery").value.toString().toFloat() != -1f){
                            holder.binding.batteryPercentage.text = tmp_ref.child("battery").value.toString()
                        }
                        if(tmp_ref.child("tension").value.toString().toFloat() != -1f){
                            holder.binding.batteryTension.text = tmp_ref.child("tension").value.toString()

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())

                }
            }
        )
        holder.binding.gateway.text = node.gatewayId
        holder.binding.nodeConnection.text = node.connection
    }

    class MyVH(view: ItemNodeBinding) : RecyclerView.ViewHolder(view.root) {
        val binding: ItemNodeBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = ItemNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

}