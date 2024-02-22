package com.best.zoom.dou.wb.fourthqrcode.zxing.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CaptureFragment.CameraInitCallBack
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CodeUtils.AnalyzeCallback

class CaptureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)
        val captureFragment = CaptureFragment()
        captureFragment.analyzeCallback = analyzeCallback
        supportFragmentManager.beginTransaction().replace(R.id.fl_zxing_container, captureFragment)
            .commit()
        captureFragment.setCameraInitCallBack(object : CameraInitCallBack {
            override fun callBack(e: Exception?) {
                if (e == null) {
                } else {
                    Log.e("TAG", "callBack: ", e)
                }
            }
        })
    }

    var analyzeCallback: AnalyzeCallback = object : AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
            bundle.putString(CodeUtils.RESULT_STRING, result)
            resultIntent.putExtras(bundle)
            this@CaptureActivity.setResult(RESULT_OK, resultIntent)
            finish()
        }

        override fun onAnalyzeFailed() {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED)
            bundle.putString(CodeUtils.RESULT_STRING, "")
            resultIntent.putExtras(bundle)
            this@CaptureActivity.setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}