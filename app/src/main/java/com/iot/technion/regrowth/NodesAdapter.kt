package com.iot.technion.regrowth

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iot.technion.regrowth.MainActivity
import com.iot.technion.regrowth.databinding.ItemNodeBinding
import com.iot.technion.regrowth.model.NodeModel


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class NodesAdapter(private val context: Context, private val nodelist: MutableMap<String, NodeModel>):
    RecyclerView.Adapter<NodesAdapter.MyVH>() {
    val TAG = "SectionsPagerAdapter"
    var nodes: MutableMap<String,NodeModel> = nodelist
    val  activity:MainActivity = context as MainActivity

    override fun getItemCount(): Int {
        return nodes.size
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        var node=nodes.toList()[position].second
        holder.binding.gateway.text = node.gatewayId
        holder.binding.batteryPercentage.text = node.battery.toString()
        holder.binding.batteryTension.text = node.tension.toString()
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