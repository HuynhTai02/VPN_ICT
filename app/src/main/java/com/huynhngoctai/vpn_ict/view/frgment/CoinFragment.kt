package com.huynhngoctai.vpn_ict.view.frgment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.huynhngoctai.vpn_ict.CommonUtils
import com.huynhngoctai.vpn_ict.view.OnDialogListenerCoin
import com.huynhngoctai.vpn_ict.databinding.FragmentCoinBinding
import com.huynhngoctai.vpn_ict.view.dialog.CheckInDailyDialog
import com.huynhngoctai.vpn_ict.view.dialog.VerifyAdsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinFragment : BaseFragment<FragmentCoinBinding>() {
    companion object {
        val TAG: String = CoinFragment::class.qualifiedName!!
        const val LAST_TAP_DATE: String = "LAST_TAP_DATE"
        const val LAST_INVITE_DATE: String = "LAST_INVITE_DATE"
        const val LAST_WATCH_DATE: String = "LAST_WATCH_DATE"
        const val LAST_DATE: String = "LAST_DATE"

        const val COUNTING_TAP: String = "COUNTING_TAP"
        const val COUNTING_WATCH: String = "COUNTING_WATCH"
        const val COUNTING_INVITE: String = "COUNTING_INVITE"

        const val DAILY_COIN_TAP: String = "DAILY_COIN_TAP"
        const val TOTAL_COIN_TAP: String = "TOTAL_COIN_TAP"

        const val DAILY_COIN_WATCH: String = "DAILY_COIN_WATCH"
        const val TOTAL_COIN_WATCH: String = "TOTAL_COIN_WATCH"

        const val DAILY_COIN_INVITE: String = "DAILY_COIN_INVITE"
        const val TOTAL_COIN_INVITE: String = "TOTAL_COIN_INVITE"

        const val MAX_TAP_COUNT: Int = 2
        const val MAX_WATCH_COUNT: Int = 2
        const val MAX_INVITE_COUNT: Int = 1
    }

    private val tapInterval: Int = 1000
    private var lastTapTime: Long = 0

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCoinBinding {
        return FragmentCoinBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        updateUI()
        showDiaNoInternet()
        addEvents()
        showBannerAds()
    }

    private fun showBannerAds() {
        val adRequest = AdRequest.Builder().build()
        binding.adBanner.loadAd(adRequest)
    }

    private fun addEvents() {
        binding.ibtBackCoin.setOnClickListener(this)
        binding.ibtCheckInCoin.setOnClickListener(this)
        binding.ibtInviteFriend.setOnClickListener(this)
        binding.ibtWatchCoin.setOnClickListener(this)
        binding.ibtTapCoin.setOnClickListener(this)
    }

    override fun clickView(v: View) {
        super.clickView(v)
        when (v) {
            binding.ibtBackCoin -> goBackMain()
            binding.ibtCheckInCoin -> showDialogCheckIn()
        }
    }

    private fun goBackMain() {
        callBack.showFragment(MainFragment.TAG, false)
    }

    private fun showDialogCheckIn() {
        val dialogCheckIn = CheckInDailyDialog(requireContext())
        dialogCheckIn.setOnDialogListener(object : OnDialogListenerCoin {
            override fun updateCoinCheckIn() {
                updateUI()
            }
        })
        dialogCheckIn.show()
    }

    private fun updateUI() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentDate = CommonUtils.getRealDay()
            val lastDate = CommonUtils.getPrefString(LAST_DATE)

            if (lastDate != currentDate) {
                CommonUtils.savePref(LAST_DATE, currentDate)
            }

            val totalCurrentCoins =
                CommonUtils.getPrefInt(VerifyAdsDialog.TOTAL_COIN_DIALOG_ADS) +
                        CommonUtils.getPrefInt(CheckInDailyDialog.TOTAL_COIN)

            val totalDailyCoins =
                if (lastDate == currentDate) {
                    CommonUtils.getPrefInt(VerifyAdsDialog.DAILY_COIN_DIALOG_ADS) +
                            CommonUtils.getPrefInt(CheckInDailyDialog.DAILY_COIN)
                } else {
                    0
                }
            withContext(Dispatchers.Main) {
                binding.tvCurrentCoins.text = totalCurrentCoins.toString()
                binding.tvDailyCoins.text = totalDailyCoins.toString()
            }
        }
    }

//    override fun clickView(v: View) {
//        super.clickView(v)
//
//        when (v) {
//            binding.ibtBackCoin -> goBackMain()
//            binding.ibtCheckInCoin -> showDialogCheckIn()
//
//            binding.ibtTapCoin -> {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val currentDate = CommonUtils.getRealDay()
//                    val lastTapDate = CommonUtils.getPrefString(LAST_TAP_DATE)
//
//                    if (lastTapDate != currentDate) {
//                        resetDailyCoinsTap(currentDate)
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        val countingTap =
//                            CommonUtils.getPrefInt(COUNTING_TAP) + 1
//                        if (countingTap <= MAX_TAP_COUNT) {
//                            CommonUtils.savePref(COUNTING_TAP, countingTap)
//                            handleActionTap()
//                        } else {
//                            showAlertDialog(
//                                "NOTIFY",
//                                "You have used all your tap attempts for today"
//                            )
//                        }
//                    }
//                }
//            }
//
//            binding.ibtWatchCoin -> {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val currentDate = CommonUtils.getRealDay()
//                    val lastWatchDate = CommonUtils.getPrefString(LAST_WATCH_DATE)
//
//                    if (lastWatchDate != currentDate) {
//                        resetDailyCoinsWatch(currentDate)
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        val countingTap =
//                            CommonUtils.getPrefInt(COUNTING_WATCH) + 1
//                        if (countingTap <= MAX_WATCH_COUNT) {
//                            CommonUtils.savePref(COUNTING_WATCH, countingTap)
//                            handleActionWatch()
//                        } else {
//                            showAlertDialog(
//                                "NOTIFY",
//                                "You have used all your tap attempts for today"
//                            )
//                        }
//                    }
//                }
//            }
//
//            binding.ibtInviteFriend -> {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val currentDate = CommonUtils.getRealDay()
//                    val lastInviteDate = CommonUtils.getPrefString(LAST_INVITE_DATE)
//
//                    if (lastInviteDate != currentDate) {
//                        resetDailyCoinsInvite(currentDate)
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        val countingInvite =
//                            CommonUtils.getPrefInt(COUNTING_INVITE) + 1
//                        if (countingInvite <= MAX_INVITE_COUNT) {
//                            CommonUtils.savePref(COUNTING_INVITE, countingInvite)
//                            handleActionInvite()
//                        } else {
//                            showAlertDialog(
//                                "NOTIFY",
//                                "You have used all your tap attempts for today"
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun resetDailyCoinsInvite(currentDate: String) {
//        CommonUtils.clearPref(COUNTING_INVITE)
//        CommonUtils.clearPref(DAILY_COIN_INVITE)
//        CommonUtils.savePref(LAST_INVITE_DATE, currentDate)
//    }
//
//    private fun resetDailyCoinsWatch(currentDate: String) {
//        CommonUtils.clearPref(COUNTING_WATCH)
//        CommonUtils.clearPref(DAILY_COIN_WATCH)
//        CommonUtils.savePref(LAST_WATCH_DATE, currentDate)
//    }
//
//    private fun resetDailyCoinsTap(currentDate: String) {
//        CommonUtils.clearPref(COUNTING_TAP)
//        CommonUtils.clearPref(DAILY_COIN_TAP)
//        CommonUtils.savePref(LAST_TAP_DATE, currentDate)
//    }
//
//    private fun resetDailyCoins(currentDate: String) {
//        CommonUtils.savePref(LAST_DATE, currentDate)
//        CommonUtils.clearPref(DAILY_COIN)
//    }
//
//    private fun handleActionTap() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val currentTime = System.currentTimeMillis()
//
//            if (currentTime - lastTapTime >= tapInterval) {
//                sumCoinRewardTap()
//
//                //last tap time update
//                lastTapTime = currentTime
//            } else {
//                withContext(Dispatchers.Main) {
//                    showAlertDialog("WARNING", "Please wait a moment before tapping again")
//                }
//            }
//        }
//    }
//
//    private fun handleActionInvite() {
//        val appUrl = "https://github.com/HuynhTai02"
//        val shareIntent = Intent(Intent.ACTION_SEND)
//
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Link VPN Master")
//        shareIntent.putExtra(
//            Intent.EXTRA_TEXT,
//            "VPN Master is a great app download and experience it with me friend!\n\nLink: $appUrl"
//        )
//        shareIntent.type = "text/plain"
//        startActivity(Intent.createChooser(shareIntent, null))
//
//        Toast.makeText(
//            context,
//            "Congratulations you have received 150 coins",
//            Toast.LENGTH_SHORT
//        ).show()
//
//        sumCoinRewardInvite()
//    }
//
//    private fun handleActionWatch() {
//        loadVideoAds("User earned the reward 150 coin.")
//        isClickableViewFalse()
//        sumCoinRewardWatch()
//    }
//
//    private fun sumCoinRewardTap() {
//        CommonUtils.getPrefInt(DAILY_COIN_TAP) + 150
//        CommonUtils.getPrefInt(TOTAL_COIN_TAP) + 150
//    }
//
//    private fun sumCoinRewardInvite() {
//        CommonUtils.getPrefInt(DAILY_COIN_INVITE) + 80
//        CommonUtils.getPrefInt(TOTAL_COIN_INVITE) + 80
//    }
//
//    private fun sumCoinRewardWatch() {
//        CommonUtils.getPrefInt(DAILY_COIN_WATCH) + 150
//        CommonUtils.getPrefInt(TOTAL_COIN_WATCH) + 150
//    }
//
//    private fun sumCoinReward() {
//        CommonUtils.getPrefInt(CheckInDailyDialog.TOTAL_COIN) + CommonUtils.getPrefInt(
//            TOTAL_COIN_TAP
//        ) + CommonUtils.getPrefInt(
//            TOTAL_COIN_WATCH
//        ) + CommonUtils.getPrefInt(TOTAL_COIN_INVITE) + CommonUtils.getPrefInt(
//            VerifyAdsDialog.TOTAL_COIN_DIALOG_ADS
//        )
//
//        CommonUtils.getPrefInt(CheckInDailyDialog.DAILY_COIN) + CommonUtils.getPrefInt(
//            DAILY_COIN_TAP
//        ) + CommonUtils.getPrefInt(
//            DAILY_COIN_WATCH
//        ) + CommonUtils.getPrefInt(DAILY_COIN_INVITE) + CommonUtils.getPrefInt(
//            VerifyAdsDialog.DAILY_COIN_DIALOG_ADS
//        )
//    }
//
//    override fun isClickableView() {
//        binding.ibtTapCoin.isClickable = true
//        binding.ibtWatchCoin.isClickable = true
//        binding.ibtInviteFriend.isClickable = true
//        binding.ibtBackCoin.isClickable = true
//        binding.ibtCheckInCoin.isClickable = true
//    }
//
//    private fun isClickableViewFalse() {
//        binding.ibtTapCoin.isClickable = false
//        binding.ibtWatchCoin.isClickable = false
//        binding.ibtInviteFriend.isClickable = false
//        binding.ibtBackCoin.isClickable = false
//        binding.ibtCheckInCoin.isClickable = false
//    }
//
//
//    private fun updateUICoin() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val currentDate = CommonUtils.getRealDay()
//            val lastDate = CommonUtils.getPrefString(LAST_DATE)
//
//            if (lastDate != currentDate) {
//                resetDailyCoins(currentDate)
//            }
//
//            sumCoinReward()
//
//            withContext(Dispatchers.Main) {
//                val currentCoins = CommonUtils.getPrefInt(TOTAL_COIN)
//                val dailyCoins = CommonUtils.getPrefInt(DAILY_COIN)
//
//                binding.tvCurrentCoins.text = currentCoins.toString()
//                binding.tvDailyCoins.text = dailyCoins.toString()
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}