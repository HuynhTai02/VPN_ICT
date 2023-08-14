package com.huynhngoctai.vpn_ict.view.dialog

import android.annotation.SuppressLint
import com.huynhngoctai.vpn_ict.R
import android.content.Context
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import com.huynhngoctai.vpn_ict.databinding.DialogRatingBinding


class RatingDialog(context: Context) : BaseDialog<DialogRatingBinding>(context),
    View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    override fun initViewBinding(): DialogRatingBinding {
        return DialogRatingBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        setUpRatingBar()
        addEvents()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpRatingBar() {
        binding.ratingBar.stepSize = 1f
        binding.ratingBar.numStars = 5
        binding.ratingImg.setImageResource(R.drawable.ic_rate_icon)
        binding.ratingText.text = "Thank you for using VPN Master app!"
    }

    private fun addEvents() {
        binding.btNo.setOnClickListener(this)
        binding.btYes.setOnClickListener(this)
        binding.ratingBar.setOnClickListener(this)
        binding.ratingBar.onRatingBarChangeListener = this
    }

    override fun clickView(v: View) {
        super.clickView(v)
        if (v == binding.btNo) {
            dismiss()
        } else {
            handleRating()
        }
    }

    private fun handleRating() {
        if (binding.ratingBar.rating > 0) {
            Toast.makeText(context, "Thank you ♥♥♥", Toast.LENGTH_SHORT).show()
            dismiss()
        } else {
            Toast.makeText(
                context,
                "Please, choose a star before pressing submit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        val countingStar: Float = rating
        val numberStar: Float = binding.ratingBar.numStars.toFloat()

        when {
            countingStar >= numberStar -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate5_icon)
                binding.ratingText.text = "We like you too! Very good ♥"
            }
            countingStar >= 4 -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate4_icon)
                binding.ratingText.text = "Good ♥"
            }
            countingStar >= 3 -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate3_icon)
                binding.ratingText.text = "Normal!"
            }
            countingStar >= 2 -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate2_icon)
                binding.ratingText.text = "Bad!"
            }
            countingStar >= 1 -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate1_icon)
                binding.ratingText.text = "Oh, No!"
            }
            else -> {
                binding.ratingImg.setImageResource(R.drawable.ic_rate_icon)
                binding.ratingText.text = "Thank you for using VPN Master app!"
            }
        }
    }
}