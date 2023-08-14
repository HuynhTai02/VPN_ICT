package com.huynhngoctai.vpn_ict.view.frgment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.huynhngoctai.vpn_ict.view.OnMainCallBack
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

abstract class BaseFragment<V : ViewBinding> : Fragment(), View.OnClickListener {
    protected lateinit var binding: V
    protected lateinit var callBack: OnMainCallBack

    private var rewardedAd: RewardedAd? = null

    fun setOnMainCallBack(callBack: OnMainCallBack) {
        this.callBack = callBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = initViewBinding(inflater, container)
        initViews()
        return binding.root
    }


    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): V

    abstract fun initViews()

    override fun onClick(v: View) {
        //do nothing
    }

    protected open fun animationView(v: View) {
        v.startAnimation(
            AnimationUtils.loadAnimation(
                context, androidx.appcompat.R.anim.abc_fade_in
            )
        )
    }

    protected open fun notify(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    protected open fun notify(msg: Int) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    protected open fun showAlertDialog(title: String, msg: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(msg)

        val alertDialog = builder.create()
        alertDialog.show()
    }

    protected fun loadVideoAds() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            requireActivity(),
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    notify(adError.toString())
                    isClickableView()

                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    notify("Ad was loaded.")
                    rewardedAd = ad
                    isClickableView()

                    rewardedAd?.let {
                        ad.show(requireActivity()) {
                            // Handle the reward.
                            notify("User earned the reward 150 coin.")
                        }
                    } ?: run {
                        notify("The rewarded ad wasn't ready yet.")
                    }
                }
            })
    }

    protected open fun isClickableView() {
        //do nothing
    }

    protected open fun showDiaNoInternet() {
        NoInternetDialogSignal.Builder(
            requireActivity(),
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }

                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Please check your Internet connection and try again!" // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
    }
}