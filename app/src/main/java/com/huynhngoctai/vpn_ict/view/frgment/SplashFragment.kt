package com.huynhngoctai.vpn_ict.view.frgment

import android.animation.Animator
import android.view.LayoutInflater
import android.view.ViewGroup
import com.huynhngoctai.vpn_ict.view.OnAnimatorListener
import com.huynhngoctai.vpn_ict.databinding.FragmentSplashBinding

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    companion object {
        //val TAG:String = SplashFragment::class.java.name
        val TAG: String = SplashFragment::class.qualifiedName!!
    }

//    private val handler = Handler(Looper.getMainLooper())

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {

        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        goToSplash()
    }

    private fun goToSplash() {
        //Use Anim
//        binding.ivSplash.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_splash))

//        Handler().postDelayed(
//            {
//                callBack.showFragment(IntroduceFragment.TAG, null, false)
//            }, Constants.TIME_DELAY
//        )

        //Recommend
//        handler.postDelayed({
//            callBack.showFragment(IntroduceFragment.TAG, null, false)
//        }, 2000)

        //Use lottie
        binding.ltaSplash.addAnimatorListener(object : OnAnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                callBack.showFragment(IntroduceFragment.TAG, false)
            }
        })

        binding.ltaSplash.playAnimation()
    }
}