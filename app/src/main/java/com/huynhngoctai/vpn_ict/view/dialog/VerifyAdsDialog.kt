package com.huynhngoctai.vpn_ict.view.dialog

import android.content.Context
import android.util.Log
import android.view.View
import com.huynhngoctai.vpn_ict.util.CommonUtils
import com.huynhngoctai.vpn_ict.databinding.DialogVerifyAdsBinding
import com.huynhngoctai.vpn_ict.view.OnDialogListenerVerifyAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerifyAdsDialog(context: Context) : BaseDialog<DialogVerifyAdsBinding>(context),
    View.OnClickListener {

    companion object {
        const val LAST_WATCH_DATE_DIALOG_ADS: String = "LAST_WATCH_DATE_DIALOG_ADS"
        const val COUNTING_WATCH_DIALOG_ADS: String = "COUNTING_WATCH_DIALOG_ADS"
        const val DAILY_COIN_DIALOG_ADS: String = "DAILY_COIN_DIALOG_ADS"
        const val TOTAL_COIN_DIALOG_ADS: String = "TOTAL_COIN_DIALOG_ADS"
        const val MAX_WATCH_COUNT_DIALOG_ADS: Int = 2
    }

    private var callBack: OnDialogListenerVerifyAds? = null

    fun setOnDialogListener(callBack: OnDialogListenerVerifyAds) {
        this.callBack = callBack
    }

    override fun initViewBinding(): DialogVerifyAdsBinding {
        return DialogVerifyAdsBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        binding.ibtCancelAds.setOnClickListener(this)
        binding.ibtWatchAds.setOnClickListener(this)
    }

    override fun clickView(v: View) {
        if (v == binding.ibtCancelAds) {
            dismiss()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val currentDate = CommonUtils.getRealDay()
                val lastWatchDate = CommonUtils.getPrefString(LAST_WATCH_DATE_DIALOG_ADS)

                if (lastWatchDate != currentDate) {
                    resetDailyCounts(currentDate)
                }

                withContext(Dispatchers.Main) {
                    val countingWatch = CommonUtils.getPrefInt(COUNTING_WATCH_DIALOG_ADS) + 1
                    if (countingWatch <= MAX_WATCH_COUNT_DIALOG_ADS) {
                        CommonUtils.savePref(COUNTING_WATCH_DIALOG_ADS, countingWatch)
                        showVideoAds()
                    } else {
                        showAlertDialog()
                    }
                }
            }
        }
    }

    private fun resetDailyCounts(currentDate: String) {
        CommonUtils.clearPref(DAILY_COIN_DIALOG_ADS)
        CommonUtils.clearPref(COUNTING_WATCH_DIALOG_ADS)
        CommonUtils.savePref(LAST_WATCH_DATE_DIALOG_ADS, currentDate)
    }

    private fun showVideoAds() {
        sumCoinReward()

        callBack?.showVideoAdsFragment()
        dismiss()
    }

    private fun sumCoinReward() {
        val totalCoinDialogAds = CommonUtils.getPrefInt(TOTAL_COIN_DIALOG_ADS) + 150
        val dailyCoinDialogAds = CommonUtils.getPrefInt(DAILY_COIN_DIALOG_ADS) + 150

        CommonUtils.savePref(TOTAL_COIN_DIALOG_ADS, totalCoinDialogAds)
        CommonUtils.savePref(DAILY_COIN_DIALOG_ADS, dailyCoinDialogAds)

        Log.d("watchAdsTotalDialog", totalCoinDialogAds.toString())
        Log.d("watchAdsDailyDialog", dailyCoinDialogAds.toString())
    }
}