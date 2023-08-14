package com.huynhngoctai.vpn_ict.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huynhngoctai.vpn_ict.R

class IntroduceAdapter(private val imageList: List<Int>) :
    RecyclerView.Adapter<IntroduceAdapter.IntroduceViewHolder>() {

    class IntroduceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    class IntroduceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imageViewIntro: ImageView
//
//        init {
//            imageViewIntro = itemView.findViewById(R.id.iv_slider_introduce)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroduceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_introduce, parent, false)
        return IntroduceViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroduceViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById<ImageView>(R.id.iv_slider_introduce)
        imageView.setBackgroundResource(imageList[position])
    }

//    override fun onBindViewHolder(holder: IntroduceViewHolder, position: Int) {
//        holder.imageViewIntro.setBackgroundResource(imageList[position])
//    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
