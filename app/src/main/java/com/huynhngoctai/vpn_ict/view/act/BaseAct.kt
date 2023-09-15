package com.huynhngoctai.vpn_ict.view.act

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.view.OnMainCallBack
import com.huynhngoctai.vpn_ict.view.frgment.BaseFragment

abstract class BaseAct<V : ViewBinding> : AppCompatActivity(), View.OnClickListener,
    OnMainCallBack {
    private lateinit var binding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initViewBinding()
        setContentView(binding.root)
        initViews()
    }

    abstract fun initViews()

    abstract fun initViewBinding(): V

    override fun onClick(v: View) {
        v.startAnimation(
            AnimationUtils.loadAnimation(
                this, androidx.appcompat.R.anim.abc_fade_in
            )
        )
        clickView(v)
    }

    protected open fun clickView(v: View) {
        //do nothing
    }

    @SuppressLint("PrivateResource", "CommitTransaction")
    override fun showFragment(tag: String, isBacked: Boolean) {
        val clazz = Class.forName(tag)
        val cons = clazz.getConstructor()
        val frg = cons.newInstance() as BaseFragment<*>

        frg.setOnMainCallBack(this)

        val trans = supportFragmentManager.beginTransaction()
        if (isBacked) {
            trans.addToBackStack(null)
        }
        trans.setCustomAnimations(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
        trans.replace(R.id.fr_container, frg, tag).commit()
    }
}