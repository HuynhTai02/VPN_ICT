package com.huynhngoctai.vpn_ict.view.frgment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.huynhngoctai.vpn_ict.CommonUtils
import com.huynhngoctai.vpn_ict.R
import com.huynhngoctai.vpn_ict.adapter.IntroduceAdapter
import com.huynhngoctai.vpn_ict.databinding.FragmentIntroduceBinding


class IntroduceFragment : BaseFragment<FragmentIntroduceBinding>() {
    companion object {
        val TAG: String = IntroduceFragment::class.qualifiedName!!
    }

    private lateinit var imgListIntro: List<Int>
    private lateinit var adapter: IntroduceAdapter
    private val stateFirstKey: String = "STATE_FIRST"

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIntroduceBinding {
        return FragmentIntroduceBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        setUpVPIntro()
        addEvents()
        checkFirstTime()
        clickAbleLinksFrText()
    }

    private fun setUpVPIntro() {
        imgListIntro =
            listOf(R.drawable.screen1, R.drawable.screen2, R.drawable.screen3, R.drawable.screen4)

        adapter = IntroduceAdapter(imgListIntro)

        binding.vp2Introduce.adapter = adapter

        addDots(0)
    }

    private fun addEvents() {
        //follow position current page
        binding.vp2Introduce.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                changeSelectedDots(position)
            }
        })

        binding.btSkip.setOnClickListener(this)
        binding.ibtAccept.setOnClickListener(this)
    }

    private fun changeSelectedDots(position: Int) {
        addDots(position)

        if (position == 0 || position == 1 || position == 2) {
            binding.ibtAccept.visibility = View.GONE
            binding.dots.visibility = View.VISIBLE
        } else {
            binding.ibtAccept.visibility = View.VISIBLE
            binding.dots.visibility = View.GONE
        }
    }

    override fun clickView(v: View) {
        super.clickView(v)
        if (v == binding.ibtAccept) {
            goToMainFragment()
        } else {
            goToMainFragment()
        }
    }

    private fun goToMainFragment() {
        //gotoMain
        callBack.showFragment(MainFragment.TAG, false)

        //save state intro screen was displayed
        CommonUtils.savePref(stateFirstKey, true)
    }

    private fun addDots(position: Int) {
        val dots = arrayOfNulls<TextView>(imgListIntro.size)
        binding.dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(activity)
            dots[i]!!.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[i]!!.textSize = 35f
            binding.dots.addView(dots[i])
        }
        dots[position]!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    //Check if don't first time use app  -> go to main screen
    private fun checkFirstTime() {
        //default variable "isFirst" is false
        val isFirst: Boolean = CommonUtils.getPrefBoolean(stateFirstKey)

        if (isFirst) {
            callBack.showFragment(MainFragment.TAG, false)
        }
    }

    private fun clickAbleLinksFrText() {
        val textPrivacy: String = resources.getString(R.string.txt_privacy_policy)
        val privacyPolicyIndex = textPrivacy.indexOf("Privacy Policy")
        val spannableString = SpannableString(textPrivacy)

        //attach onClick event to "Privacy Policy"
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val privacyPolicyUrl =
                    "https://www.freeprivacypolicy.com/blog/privacy-policy-android-apps/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = Color.GREEN
            }
        }

        //set ClickableSpan to "Privacy Policy" to make it clickable
        spannableString.setSpan(
            clickableSpan,
            privacyPolicyIndex,
            privacyPolicyIndex + "Privacy Policy".length,
            0
        )

        //set formatted SpannableString to the TextView
        binding.tvPrivacyPolicy.text = spannableString
        //enable LinkMovementMethod to make "Privacy Policy" clickable
        binding.tvPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
    }
}