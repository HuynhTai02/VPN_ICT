package com.huynhngoctai.vpn_ict.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.model.CountryWire
import com.huynhngoctai.vpn_ict.model.ServerWire
import de.hdodenhof.circleimageview.CircleImageView

class CountryWireAdapter(
    private val countries: List<CountryWire>,
    private val clickListener: OnServerItemClickListener? = null
) :
    RecyclerView.Adapter<CountryWireAdapter.CountryViewHolder>() {

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryImageView: CircleImageView
        val countryNameTextView: TextView
        val serversRecyclerView: RecyclerView
        val arrowDown: ImageView

        init {
            arrowDown = itemView.findViewById(R.id.imv_arrow)
            countryImageView = itemView.findViewById(R.id.imv_country)
            countryNameTextView = itemView.findViewById(R.id.tv_country_name)
            serversRecyclerView = itemView.findViewById(R.id.rv_servers)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val currentCountry = countries[position]
        holder.countryImageView.setImageResource(currentCountry.flagCountry)
        holder.countryNameTextView.text = currentCountry.nameCountry

        holder.serversRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.serversRecyclerView.adapter = ServerWireAdapter(currentCountry.serverWires, clickListener!!)

        holder.arrowDown.setOnClickListener {
            if (holder.serversRecyclerView.visibility == View.GONE) {
                holder.serversRecyclerView.visibility = View.VISIBLE
                holder.arrowDown.setImageResource(R.drawable.up)
            } else {
                holder.arrowDown.setImageResource(R.drawable.down)
                holder.serversRecyclerView.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            if (currentCountry.serverWires.isNotEmpty()) {
                val firstServer = currentCountry.serverWires[0]
                clickListener.onServerItemClicked(firstServer)
            }
        }
    }

    override fun getItemCount() = countries.size

    interface OnServerItemClickListener {
        fun onServerItemClicked(serverWire: ServerWire)
    }
}