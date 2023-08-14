package com.huynhngoctai.vpn_ict.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.huynhngoctai.vpn_ict.CommonUtils
import com.huynhngoctai.vpn_ict.view.OnDialogListenerCoin
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.databinding.DialogCheckinDailyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckInDailyDialog(context: Context) : BaseDialog<DialogCheckinDailyBinding>(context),
    View.OnClickListener {

    companion object {
        const val CHECK_IN_DAY_INDEX = "CHECK_IN_DAY_INDEX"
        const val LAST_CHECK_IN_DATE = "LAST_CHECK_IN_DATE"
        const val TOTAL_COIN = "TOTAL_COIN"
        const val DAILY_COIN = "DAILY_COIN"
    }

    private var callBack: OnDialogListenerCoin? = null

    fun setOnDialogListener(callBack: OnDialogListenerCoin) {
        this.callBack = callBack
    }

    private val dayTextViews: MutableList<TextView> = mutableListOf()
    private val coinValues = arrayOf(50, 100, 200, 250, 300, 300, 1000)

    override fun initViewBinding(): DialogCheckinDailyBinding {
        return DialogCheckinDailyBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        addEvents()
        addDayTextView()
        loadData()
    }

    private fun loadData() {
        val currentDayIndex = CommonUtils.getPrefInt(CHECK_IN_DAY_INDEX)
        binding.tvCountDay.text = currentDayIndex.toString()
        for (i in 0 until currentDayIndex) {
            dayTextViews[i].setBackgroundResource(R.drawable.checked)
        }
    }

    private fun addDayTextView() {
        dayTextViews.add(binding.tvCheckDay1)
        dayTextViews.add(binding.tvCheckDay2)
        dayTextViews.add(binding.tvCheckDay3)
        dayTextViews.add(binding.tvCheckDay4)
        dayTextViews.add(binding.tvCheckDay5)
        dayTextViews.add(binding.tvCheckDay6)
        dayTextViews.add(binding.tvCheckDay7)
    }

    override fun setUpDialog() {
        super.setUpDialog()
        setCanceledOnTouchOutside(true)
    }

    private fun addEvents() {
        binding.ibtCheckIn.setOnClickListener(this)
    }

    override fun clickView(v: View) {
        super.clickView(v)
        if (v == binding.ibtCheckIn) {
            handleCheckIn()
        }
    }

    private fun handleCheckIn() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastCheckInDate = CommonUtils.getPrefString(LAST_CHECK_IN_DATE)
            val currentDate = CommonUtils.getRealDay()

            if (lastCheckInDate == currentDate) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "You have already checked in today!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val currentDayIndex = CommonUtils.getPrefInt(CHECK_IN_DAY_INDEX)
                val totalCoins = CommonUtils.getPrefInt(TOTAL_COIN)

                if (currentDayIndex >= 7) {

                    CommonUtils.clearPref(CHECK_IN_DAY_INDEX)
                    CommonUtils.clearPref(DAILY_COIN)

                    withContext(Dispatchers.Main) {
                        for (dayTextView in dayTextViews) {
                            dayTextView.setBackgroundResource(R.drawable.check_none_dailycheck)
                        }
                        binding.tvCountDay.text = "0"
                    }
                } else {
                    val updatedDayIndex = currentDayIndex + 1

                    CommonUtils.savePref(CHECK_IN_DAY_INDEX, updatedDayIndex)
                    val coinValue = coinValues[currentDayIndex]
                    val coinEarned = CommonUtils.getPrefInt(DAILY_COIN) + coinValue
                    CommonUtils.savePref(DAILY_COIN, coinEarned)

                    withContext(Dispatchers.Main) {
                        val dayTextView = dayTextViews[currentDayIndex]
                        dayTextView.setBackgroundResource(R.drawable.checked)
                        binding.tvCountDay.text = "$updatedDayIndex"

                        val newTotalCoins = totalCoins + coinValue
                        CommonUtils.savePref(TOTAL_COIN, newTotalCoins)

                        Toast.makeText(
                            context,
                            "Congratulations you have received $coinEarned coins!",
                            Toast.LENGTH_SHORT
                        ).show()

                        callBack?.updateCoinCheckIn()
                    }
                }

                CommonUtils.savePref(LAST_CHECK_IN_DATE, currentDate)
            }
        }
    }
}