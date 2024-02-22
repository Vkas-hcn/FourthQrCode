package com.best.zoom.dou.wb.fourthqrcode.scan

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityScanQrBinding
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CaptureFragment
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CodeUtils
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CodeUtils.analyzeBitmap

class ScanQrCode  : BaseActivity<ScanViewModel, ActivityScanQrBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_scan_qr
    }

    override fun getViewModelClass(): Class<ScanViewModel> {
        return ScanViewModel::class.java
    }

    override fun initViews() {
        setQrScan()
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgChoose.setOnClickListener {
            toClickGallery()
        }
        binding.imgFlash.setOnClickListener {
            toClickFlash()
        }
    }

    var isOpen = false
    private fun setQrScan() {
        val analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
            override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                toEndPage(result)
            }

            override fun onAnalyzeFailed() {
                Toast.makeText(
                    applicationContext, "Identification failed, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val captureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment, R.layout.auto_qr)
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment)
            .commit()
    }


    private fun toClickGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 0x9527)
    }
    fun toClickFlash(){
        isOpen =!isOpen
        CodeUtils.isLightEnable(isOpen)
    }

    fun toEndPage(data:String){
        if (data.isNotEmpty()) {
            navigateToWithParams(ScanResult::class.java, Bundle().apply {
                putString("result_text",data)
            })
            finish()
        } else {
            Toast.makeText(
                applicationContext, "Identification failed, please try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x9527) {
            if (data != null) {
                val uri: Uri? = data.data
                val cr = contentResolver
                try {
                    val mBitmap = MediaStore.Images.Media.getBitmap(cr, uri)
                    analyzeBitmap(mBitmap,object : CodeUtils.AnalyzeCallback {
                        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                            toEndPage(result)
                        }

                        override fun onAnalyzeFailed() {
                            Toast.makeText(this@ScanQrCode, "Identification failed, please try again", Toast.LENGTH_LONG).show()
                        }
                    })
                    mBitmap?.recycle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}