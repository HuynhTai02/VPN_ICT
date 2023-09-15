package com.huynhngoctai.vpn_ict.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.model.AppInfo

class AppInfoAdapter(private var apps: List<AppInfo>) :
    RecyclerView.Adapter<AppInfoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.app_icon)
        val appName: TextView = itemView.findViewById(R.id.app_name)
        val appSelectCheckbox: CheckBox = itemView.findViewById(R.id.app_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = apps[position]
        holder.appName.text = appInfo.name
        holder.appIcon.setImageDrawable(appInfo.icon)
        holder.appSelectCheckbox.isChecked = appInfo.isSelected

        holder.appSelectCheckbox.setOnCheckedChangeListener { _, isChecked ->
            appInfo.isSelected = isChecked
        }
    }
}