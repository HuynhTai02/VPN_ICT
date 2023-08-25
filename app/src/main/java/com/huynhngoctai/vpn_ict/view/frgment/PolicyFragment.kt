package com.huynhngoctai.vpn_ict.view.frgment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.huynhngoctai.vpn_ict.databinding.FragmentPolicyBinding

class PolicyFragment : BaseFragment<FragmentPolicyBinding>() {
    companion object {
        val TAG: String = PolicyFragment::class.qualifiedName!!
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPolicyBinding {
        return FragmentPolicyBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        // Configure WebView
        configWebView()
        addEvents()
    }

    private fun addEvents() {
        binding.btBack.setOnClickListener(this)
        binding.btHome.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        animationView(v)

        if (v == binding.btBack) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                callBack.showFragment(MainFragment.TAG, false)
            }
        } else {
            callBack.showFragment(MainFragment.TAG, false)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        // Configure WebViewClient to handle loading of website content
        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        val url = "https://www.freeprivacypolicy.com/blog/privacy-policy-android-apps/"
        binding.webView.loadUrl(url)
    }
}