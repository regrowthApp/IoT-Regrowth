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
    val TAG = "NodesPagerAdapter"
    var nodes: MutableMap<String,NodeModel> = nodelist
    var animal : String = animal_name
    val activity:MainActivity = context as MainActivity
    val ref = FirebaseDatabase.getInstance()

    override fun getItemCount(): Int {
        return nodes.size
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        var node=nodes.toList()[position].second
        var battery_sent = false
        var tension_sent = false
        ref.reference.child("users/${uid}/${animal}").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("nodes/${node.nodeId}")){
                        val tmp_ref = dataSnapshot.child("nodes/${node.nodeId}")
                        if(tmp_ref.child("battery").value.toString() != ""){
                            holder.binding.batteryPercentage.text = tmp_ref.child("battery").value.toString()
                            battery_sent = true
                        }
                        if(tmp_ref.child("tension").value.toString()  != ""){
                            holder.binding.batteryTension.text = tmp_ref.child("tension").value.toString()
                            tension_sent = true
                        }

                        if(battery_sent and tension_sent){
                            holder.binding.nodeConnection.text = "connected"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        val view = ItemNodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyVH(view)
    }

}