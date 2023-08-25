package com.huynhngoctai.vpn_ict.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.model.Servers

class ServersAdapter(
    private val servers: List<Servers>,
    private val clickListener: CountriesAdapter.OnServerItemClickListener
) :
    RecyclerView.Adapter<ServersAdapter.ServerViewHolder>() {

    class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityNameTextView: TextView

        init {
            cityNameTextView = itemView.findViewById(R.id.tv_city)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val currentServer = servers[position]
        holder.cityNameTextView.text = currentServer.nameCity
        holder.itemView.setOnClickListener {
            clickListener.onServerItemClicked(currentServer) }
    }

    override fun getItemCount() = servers.size
}
