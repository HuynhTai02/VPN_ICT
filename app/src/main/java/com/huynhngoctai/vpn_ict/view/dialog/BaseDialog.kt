package com.huynhngoctai.vpn_ict.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<V : ViewBinding>(context: Context) : Dialog(context),
    View.OnClickListener {
    protected lateinit var binding: V
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = initViewBinding()
        setContentView(binding.root)

        initViews()
    }

    protected open fun initViews() {
        setUpDialog()
    }

    abstract fun initViewBinding(): V

    protected open fun setUpDialog() {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = 1000
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes = layoutParams

        setCanceledOnTouchOutside(false)
    }

    override fun onClick(v: View) {
        v.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                androidx.appcompat.R.anim.abc_fade_in
            )
        )

        handler.postDelayed({
            clickView(v)
        }, 100)
    }

    protected open fun clickView(v: View) {
        //do nothing
    }

    protected open fun showAlertDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("NOTIFY")
        builder.setMessage("You have used all your tap attempts for today!")

        val alertDialog = builder.create()
        alertDialog.show()
    }
}