package com.best.zoom.dou.wb.fourthqrcode.setting

import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivitySettingBinding
import com.best.zoom.dou.wb.fourthqrcode.web.NetPrivacyPolicy

class SettingNet : BaseActivity<SettingNetViewModel, ActivitySettingBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_setting
    }

    override fun getViewModelClass(): Class<SettingNetViewModel> {
        return SettingNetViewModel::class.java
    }

    override fun initViews() {
        binding.tvPrivacyPolicy.setOnClickListener {
            navigateTo(NetPrivacyPolicy::class.java)
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}