package com.huynhngoctai.vpn_ict.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.model.ServerWire

class ServerWireAdapter(
    private val serverWires: List<ServerWire>,
    private val clickListener: CountryWireAdapter.OnServerItemClickListener
) :
    RecyclerView.Adapter<ServerWireAdapter.ServerViewHolder>() {

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
        val currentServer = serverWires[position]
        holder.cityNameTextView.text = currentServer.nameCity
        holder.itemView.setOnClickListener {
            clickListener.onServerItemClicked(currentServer)
        }
    }

    override fun getItemCount() = serverWires.size
}
