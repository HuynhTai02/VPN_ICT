package com.huynhngoctai.vpn_ict.view.frgment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.huynhngoctai.vpn_ict.util.CommonUtils
import com.huynhngoctai.vpn_ict.view.OnDialogListenerCheckIn
import com.huynhngoctai.vpn_ict.databinding.FragmentCoinBinding
import com.huynhngoctai.vpn_ict.view.OnDialogListenerVerifyAds
import com.huynhngoctai.vpn_ict.view.dialog.CheckInDailyDialog
import com.huynhngoctai.vpn_ict.view.dialog.VerifyAdsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoinFragment : BaseFragment<FragmentCoinBinding>() {
    companion object {
        val TAG: String = CoinFragment::class.qualifiedName!!
        const val LAST_TAP_DATE: String = "LAST_TAP_DATE"
        const val LAST_INVITE_DATE: String = "LAST_INVITE_DATE"
        const val LAST_DATE: String = "LAST_DATE"

        const val COUNTING_TAP: String = "COUNTING_TAP"
        const val COUNTING_INVITE: String = "COUNTING_INVITE"

        const val DAILY_COIN_TAP: String = "DAILY_COIN_TAP"
        const val TOTAL_COIN_TAP: String = "TOTAL_COIN_TAP"

        const val DAILY_COIN_INVITE: String = "DAILY_COIN_INVITE"
        const val TOTAL_COIN_INVITE: String = "TOTAL_COIN_INVITE"

        const val MAX_TAP_COUNT: Int = 2
        const val MAX_INVITE_COUNT: Int = 1
    }

    private var dialogCheckIn: CheckInDailyDialog? = null
    private var verifyAdsDialog: VerifyAdsDialog? = null
    private val tapInterval: Int = 1000
    private var lastTapTime: Long = 0

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCoinBinding {
        return FragmentCoinBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        showDiaNoInternet()
        updateUI()
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

    private fun goBackMain() {
        callBack.showFragment(MainFragment.TAG, false)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        animationView(v)

        when (v) {
            binding.ibtBackCoin -> goBackMain()
            binding.ibtCheckInCoin -> showDialogCheckIn()
            binding.ibtWatchCoin -> showDialogVerifyAds()
            binding.ibtTapCoin -> handleTapCoin()
            binding.ibtInviteFriend -> handleInvite()
        }
    }

    private fun handleInvite() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentDate = CommonUtils.getRealDay()
            val lastInviteDate = CommonUtils.getPrefString(LAST_INVITE_DATE)

            if (lastInviteDate != currentDate) {
                resetDailyCoinsInvite(currentDate)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val countingInvite =
                    CommonUtils.getPrefInt(COUNTING_INVITE) + 1
                if (countingInvite <= MAX_INVITE_COUNT) {

                    actionSendApp(countingInvite)

                    lifecycleScope.launch(Dispatchers.Main) {
                        notify("Congratulations you have received 80 coins")
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        showAlertDialog(
                            "NOTIFY",
                            "You have used all your tap attempts for today"
                        )
                    }
                }
            }
        }
    }

    private fun resetDailyCoinsInvite(currentDate: String) {
        CommonUtils.clearPref(COUNTING_INVITE)
        CommonUtils.savePref(LAST_INVITE_DATE, currentDate)
    }

    private fun savedValueHandleInvite(countingInvite: Int) {
        CommonUtils.savePref(COUNTING_INVITE, countingInvite)

        CommonUtils.savePref(
            DAILY_COIN_INVITE,
            CommonUtils.getPrefInt(DAILY_COIN_INVITE) + 80
        )

        CommonUtils.savePref(
            TOTAL_COIN_INVITE,
            CommonUtils.getPrefInt(TOTAL_COIN_INVITE) + 80
        )
    }

    private fun actionSendApp(countingInvite: Int) {
        val appUrl = "https://github.com/HuynhTai02"
        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Link VPN Master")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "VPN Master is a great app download and experience it with me friend!\n\nLink: $appUrl"
        )
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, null))

        savedValueHandleInvite(countingInvite)
    }

    private fun resetDailyCoinsTap(currentDate: String) {
        CommonUtils.clearPref(COUNTING_TAP)
        CommonUtils.savePref(LAST_TAP_DATE, currentDate)
    }

    private fun handleTapCoin() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentDate = CommonUtils.getRealDay()
            val lastTapDate = CommonUtils.getPrefString(LAST_TAP_DATE)

            if (lastTapDate != currentDate) {
                resetDailyCoinsTap(currentDate)
            }

            val countingTap =
                CommonUtils.getPrefInt(COUNTING_TAP) + 1
            Log.d(TAG, "handleTapCoin: $countingTap")
            if (countingTap <= MAX_TAP_COUNT) {

                //Check time between 2 tap
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTapTime >= tapInterval) {

                    savedValueHandleTap(countingTap)

                    lifecycleScope.launch(Dispatchers.Main) {
                        updateUI()

                        notify("Congratulations you have received 150 coins")
                    }

                    //last tap time update
                    lastTapTime = currentTime
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        showAlertDialog(
                            "WARNING",
                            "Please wait a moment before tapping again"
                        )
                    }
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    showAlertDialog(
                        "NOTIFY",
                        "You have used all your tap attempts for today"
                    )
                }
            }
        }
    }

    private fun savedValueHandleTap(countingTap: Int) {
        CommonUtils.savePref(COUNTING_TAP, countingTap)

        CommonUtils.savePref(
            DAILY_COIN_TAP,
            CommonUtils.getPrefInt(DAILY_COIN_TAP) + 150
        )

        CommonUtils.savePref(
            TOTAL_COIN_TAP,
            CommonUtils.getPrefInt(TOTAL_COIN_TAP) + 150
        )
    }

    private fun showDialogVerifyAds() {
        verifyAdsDialog = VerifyAdsDialog(requireContext())
        verifyAdsDialog!!.setOnDialogListener(object : OnDialogListenerVerifyAds {
            override fun showVideoAdsFragment() {
                loadVideoAds()
                isClickableViewFalse()
            }
        })
        verifyAdsDialog!!.show()
    }

    override fun isClickableView() {
        binding.ibtTapCoin.isClickable = true
        binding.ibtWatchCoin.isClickable = true
        binding.ibtInviteFriend.isClickable = true
        binding.ibtBackCoin.isClickable = true
        binding.ibtCheckInCoin.isClickable = true
    }

    private fun isClickableViewFalse() {
        binding.ibtTapCoin.isClickable = false
        binding.ibtWatchCoin.isClickable = false
        binding.ibtInviteFriend.isClickable = false
        binding.ibtBackCoin.isClickable = false
        binding.ibtCheckInCoin.isClickable = false
    }

    private fun showDialogCheckIn() {
        dialogCheckIn = CheckInDailyDialog(requireContext())
        dialogCheckIn!!.setOnDialogListener(object : OnDialogListenerCheckIn {
            override fun updateCoinCheckIn() {
                updateUI()
            }
        })
        dialogCheckIn!!.show()
    }

    private fun fetchTotalDailyCoins(): Int {
        return CommonUtils.getPrefInt(VerifyAdsDialog.DAILY_COIN_DIALOG_ADS) +
                CommonUtils.getPrefInt(CheckInDailyDialog.DAILY_COIN) +
                CommonUtils.getPrefInt(DAILY_COIN_TAP) +
                CommonUtils.getPrefInt(DAILY_COIN_INVITE)
    }

    private fun fetchTotalCurrentCoins(): Int {
        return CommonUtils.getPrefInt(VerifyAdsDialog.TOTAL_COIN_DIALOG_ADS) +
                CommonUtils.getPrefInt(CheckInDailyDialog.TOTAL_COIN) +
                CommonUtils.getPrefInt(TOTAL_COIN_TAP) +
                CommonUtils.getPrefInt(TOTAL_COIN_INVITE)
    }

    private fun updateUI() {
        lifecycleScope.launch(Dispatchers.IO) {

            // Reset total daily coins if it's a new day
            if (isTodayNewDay()) {
                checkIsResetDialog()
                CommonUtils.clearPref(DAILY_COIN_TAP)
                CommonUtils.clearPref(DAILY_COIN_INVITE)
                CommonUtils.savePref(LAST_DATE, CommonUtils.getRealDay())
            }

            lifecycleScope.launch(Dispatchers.Main) {

                val totalCurrentCoins = fetchTotalCurrentCoins()
                binding.tvCurrentCoins.text = totalCurrentCoins.toString()

                val totalDailyCoins = fetchTotalDailyCoins()
                binding.tvDailyCoins.text = totalDailyCoins.toString()

                Log.d(
                    "UpdateUICoinFragment",
                    "total: ${binding.tvCurrentCoins.text}, daily: ${binding.tvDailyCoins.text} "
                )
            }
        }
    }

    private suspend fun isTodayNewDay(): Boolean {
        val currentDate = CommonUtils.getRealDay()
        val lastDate = CommonUtils.getPrefString(LAST_DATE)
        return lastDate != currentDate
    }

    private fun checkIsResetDialog() {
        if (dialogCheckIn?.isResetDailyCoinCheckIn == true) {
            CommonUtils.clearPref(VerifyAdsDialog.DAILY_COIN_DIALOG_ADS)

            dialogCheckIn!!.isResetDailyCoinCheckIn = false
        } else {
            CommonUtils.clearPref(VerifyAdsDialog.DAILY_COIN_DIALOG_ADS)
        }

        if (verifyAdsDialog?.isResetDailyCoinWatchAds == true) {
            CommonUtils.clearPref(CheckInDailyDialog.DAILY_COIN)
            verifyAdsDialog!!.isResetDailyCoinWatchAds = false
        } else {
            CommonUtils.clearPref(CheckInDailyDialog.DAILY_COIN)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}