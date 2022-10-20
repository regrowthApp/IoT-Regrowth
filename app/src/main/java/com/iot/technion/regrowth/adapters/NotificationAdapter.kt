package com.iot.technion.regrowth.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iot.technion.regrowth.R
import com.iot.technion.regrowth.model.NotificationModel

class NotificationAdapter(private val notificationList: List<NotificationModel>) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notificationList[position]
        holder.title.text = currentItem.title
        holder.body.text = currentItem.body
        //convert timestamp to date
        var date = java.util.Date(currentItem.timestamp)
        //format date
        var format = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        holder.timestamp.text = format.format(date)
    }

    override fun getItemCount() = notificationList.size

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val body: TextView = itemView.findViewById(R.id.tvMessage)
        val timestamp: TextView = itemView.findViewById(R.id.tvTime)
    }
}