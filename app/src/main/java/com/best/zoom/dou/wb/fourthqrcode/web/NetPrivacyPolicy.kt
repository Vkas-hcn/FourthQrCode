package com.best.zoom.dou.wb.fourthqrcode.web

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityNetPrivacyBinding

class NetPrivacyPolicy : BaseActivity<NetPrivacyViewModel, ActivityNetPrivacyBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_net_privacy
    }

    override fun getViewModelClass(): Class<NetPrivacyViewModel> {
        return NetPrivacyViewModel::class.java
    }

    override fun initViews() {
        initWebView()
    }
    private fun initWebView() {
        binding.webView.loadUrl("https://www.baidu.com")
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }
    }
}