package com.best.zoom.dou.wb.fourthqrcode.scan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityScanQrBinding
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityScanResultBinding

class ScanResult : BaseActivity<ScanViewModel, ActivityScanResultBinding>() {
    var resultData = ""
    override fun getLayoutRes(): Int {
        return R.layout.activity_scan_result
    }

    override fun getViewModelClass(): Class<ScanViewModel> {
        return ScanViewModel::class.java
    }

    override fun initViews() {
        resultData = intent.getStringExtra("result_text") ?: ""
        binding.tvResult.text = resultData
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.llCopy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", resultData)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.llSs.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val content_url = Uri.parse("https://www.google.com/search?q=${resultData}")
            intent.data = content_url
            startActivity(intent)
        }
        binding.llShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, resultData)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share"))
        }
    }
}