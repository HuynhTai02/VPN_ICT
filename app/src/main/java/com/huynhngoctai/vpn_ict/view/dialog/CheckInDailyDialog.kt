package com.huynhngoctai.vpn_ict.view.dialog

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.huynhngoctai.vpn_ict.util.CommonUtils
import com.huynhngoctai.vpn_ict.view.OnDialogListenerCheckIn
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

    private var callBack: OnDialogListenerCheckIn? = null
    private var isCheckingIn = false

    fun setOnDialogListener(callBack: OnDialogListenerCheckIn) {
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
        dayTextViews.addAll(
            listOf(
                binding.tvCheckDay1,
                binding.tvCheckDay2,
                binding.tvCheckDay3,
                binding.tvCheckDay4,
                binding.tvCheckDay5,
                binding.tvCheckDay6,
                binding.tvCheckDay7
            )
        )
    }

    override fun setUpDialog() {
        super.setUpDialog()
        setCanceledOnTouchOutside(true)
    }

    private fun addEvents() {
        binding.ibtCheckIn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        v.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                androidx.appcompat.R.anim.abc_popup_enter
            )
        )

        if (v == binding.ibtCheckIn && !isCheckingIn) {
            isCheckingIn = true
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
                    isCheckingIn = false
                }
            } else {
                val currentDayIndex = CommonUtils.getPrefInt(CHECK_IN_DAY_INDEX)

                if (currentDayIndex >= 7) {
                    resetCheckInState()
                } else {
                    updateCheckInState(currentDayIndex)
                }

                CommonUtils.savePref(LAST_CHECK_IN_DATE, currentDate)
                isCheckingIn = false
            }
        }
    }

    private suspend fun resetCheckInState() {
        CommonUtils.clearPref(CHECK_IN_DAY_INDEX)
        CommonUtils.clearPref(DAILY_COIN)

        withContext(Dispatchers.Main) {
            dayTextViews.forEach { it.setBackgroundResource(R.drawable.check_none_dailycheck) }
            binding.tvCountDay.text = "0"
        }
    }

    private suspend fun updateCheckInState(currentDayIndex: Int) {
        val updatedDayIndex = currentDayIndex + 1
        CommonUtils.savePref(CHECK_IN_DAY_INDEX, updatedDayIndex)

        val coinDaily = coinValues[currentDayIndex]
        val coinTotal = CommonUtils.getPrefInt(TOTAL_COIN) + coinDaily
        CommonUtils.savePref(DAILY_COIN, coinDaily)
        CommonUtils.savePref(TOTAL_COIN, coinTotal)

        withContext(Dispatchers.Main) {
            val dayTextView = dayTextViews[currentDayIndex]
            dayTextView.setBackgroundResource(R.drawable.checked)
            binding.tvCountDay.text = "$updatedDayIndex"
            Toast.makeText(
                context,
                "Congratulations you have received $coinDaily coins!",
                Toast.LENGTH_SHORT
            ).show()

            callBack?.updateCoinCheckIn()
        }
    }
}