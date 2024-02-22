package com.best.zoom.dou.wb.fourthqrcode.create

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.best.zoom.dou.wb.fourthqrcode.App
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class CreateQrCodeModel:ViewModel() {
    fun hasWriteExternalStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            ContextCompat.checkSelfPermission(
                App.instance,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestWriteExternalStoragePermission(activity: Activity,requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            requestCode
        )
    }

    fun saveBitmapToGallery(bitmap: Bitmap, onSaveComplete: (Boolean) -> Unit) {
        if (!hasWriteExternalStoragePermission()) {
            onSaveComplete(false)
            return
        }

        val filename = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = App.instance.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            imageUri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
                outputStream.close()
                Toast.makeText( App.instance, "Saved successfully", Toast.LENGTH_SHORT).show()
                onSaveComplete(true)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onSaveComplete(false)
    }

    fun shareBitmap(bitmap: Bitmap,context: Context) {
        val uri: Uri = getImageUri(context, bitmap)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }


    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "shared_image.png")

        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imageFile)
    }
}