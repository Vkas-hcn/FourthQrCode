package com.best.zoom.dou.wb.fourthqrcode.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.base.BaseActivity
import com.best.zoom.dou.wb.fourthqrcode.create.CreateQrCode
import com.best.zoom.dou.wb.fourthqrcode.databinding.ActivityMainBinding
import com.best.zoom.dou.wb.fourthqrcode.scan.ScanQrCode
import com.best.zoom.dou.wb.fourthqrcode.setting.SettingNet

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun initViews() {
        binding.imgSettings.setOnClickListener {
            navigateTo(SettingNet::class.java)
        }
        binding.clTop.setOnClickListener {
            requestCameraPermission{
                navigateTo(ScanQrCode::class.java)
            }
        }
        binding.textCreateQr.setOnClickListener {
            navigateTo(CreateQrCode::class.java)
        }
    }
    private val CAMERA_PERMISSION_CODE = 65845

    private fun requestCameraPermission(nextFun:()->Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            nextFun()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               navigateTo(ScanQrCode::class.java)
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission denied")
            .setMessage("You need to grant camera permissions to use this feature")
            .setPositiveButton("Go to settings") { dialog, _ ->
                goToAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun goToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}