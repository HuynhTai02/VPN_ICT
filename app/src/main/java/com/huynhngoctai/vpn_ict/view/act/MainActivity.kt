package com.huynhngoctai.vpn_ict.view.act

import com.huynhngoctai.vpn_ict.CommonUtils
import com.huynhngoctai.vpn_ict.databinding.ActivityMainBinding
import com.huynhngoctai.vpn_ict.view.frgment.SplashFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseAct<ActivityMainBinding>() {

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        goToSplash()
    }

    private fun goToSplash() {
        showFragment(SplashFragment.TAG, false)
    }
}