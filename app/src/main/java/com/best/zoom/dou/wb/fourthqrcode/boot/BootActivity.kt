package com.best.zoom.dou.wb.fourthqrcode.boot

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityBootBinding
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityMainBinding
import com.best.zoom.dou.wb.fourthqrcode.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BootActivity : BaseActivity<BootViewModel, ActivityBootBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_boot
    }

    override fun getViewModelClass(): Class<BootViewModel> {
        return BootViewModel::class.java
    }

    override fun initViews() {
        startCountdown()
    }
    private fun startCountdown() {
        lifecycleScope.launch {
            for (i in 0..100) {
                binding.progressBar.progress = i
                delay(20)
            }
            navigateTo(MainActivity::class.java)
            finish()
        }
    }
}