package com.best.zoom.dou.wb.fourthqrcode.create

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityCreateQrBinding
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityMainBinding
import com.best.zoom.dou.wb.fourthqrcode.main.MainViewModel
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CodeUtils

class CreateQrCode : BaseActivity<CreateQrCodeModel, ActivityCreateQrBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_create_qr
    }

    override fun getViewModelClass(): Class<CreateQrCodeModel> {
        return CreateQrCodeModel::class.java
    }

    override fun initViews() {
        setupListeners()
        clickFun()
    }
    private fun saveBitmap(bitmap: Bitmap) {
        val requestCode = 2333
        if (!viewModel.hasWriteExternalStoragePermission()) {
            viewModel.requestWriteExternalStoragePermission(this,requestCode)
        } else {
            viewModel.saveBitmapToGallery(bitmap){}
        }
    }
    private fun clickFun(){
        binding.llSave.setOnClickListener { saveBitmap(binding.imgQrCode.drawable.toBitmap()) }
        binding.llShare.setOnClickListener { viewModel.shareBitmap(binding.imgQrCode.drawable.toBitmap(),this) }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupListeners() {
        binding.textView.setOnClickListener { finish() }
        binding.tvGenerate.setOnClickListener {
            binding.etResult.text.toString().takeIf { it.isNotEmpty() }?.let { content ->
                showQrCode()
                binding.imgQrCode.setImageBitmap(CodeUtils.createImage(content, 103, 102, null))
            } ?: run {
                Toast.makeText(this, "Please enter the content to be generated", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.etResult.addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                binding.tvGenerate.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_save))
                binding.tvGenerate.setTextColor(resources.getColor(R.color.save_enabled))
            } else {
                binding.tvGenerate.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_main_1))
                binding.tvGenerate.setTextColor(resources.getColor(R.color.save_disabled))
            }
        }
    }

    private fun showQrCode() {
        binding.linearLayout2.visibility = View.VISIBLE
        binding.llSave.visibility = View.VISIBLE
        binding.llShare.visibility = View.VISIBLE
        binding.linearLayout.visibility = View.GONE
        binding.tvTip.visibility = View.GONE
        binding.tvGenerate.visibility = View.GONE
        closeKeyBoard()
    }
    private fun closeKeyBoard() {
        val view = this.window.peekDecorView()
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}