package com.best.zoom.dou.wb.fourthqrcode.zxing.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Bundle
import android.text.TextUtils
import com.best.zoom.dou.wb.fourthqrcode.zxing.camera.BitmapLuminanceSource
import com.best.zoom.dou.wb.fourthqrcode.zxing.camera.CameraManager
import com.best.zoom.dou.wb.fourthqrcode.zxing.decoding.DecodeFormatManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Result
import com.google.zxing.WriterException
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable
import java.util.Vector

object CodeUtils {
    const val RESULT_TYPE = "result_type_qr"
    const val RESULT_STRING = "result_string_qr"
    const val RESULT_SUCCESS = 1
    const val RESULT_FAILED = 2
    const val LAYOUT_ID = "layout_id"
    fun analyzeBitmap(mBitmap: Bitmap?, analyzeCallback: AnalyzeCallback?) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inJustDecodeBounds = false
        var sampleSize = (options.outHeight / 400f).toInt()
        if (sampleSize <= 0) sampleSize = 1
        options.inSampleSize = sampleSize
        val multiFormatReader = MultiFormatReader()
        val hints = Hashtable<DecodeHintType, Any?>(2)
        var decodeFormats = Vector<BarcodeFormat?>()
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = Vector()
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        multiFormatReader.setHints(hints)
        var rawResult: Result? = null
        try {
            rawResult = multiFormatReader.decodeWithState(
                BinaryBitmap(
                    HybridBinarizer(
                        BitmapLuminanceSource(mBitmap)
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (rawResult != null) {
            analyzeCallback?.onAnalyzeSuccess(mBitmap, rawResult.text)
        } else {
            analyzeCallback?.onAnalyzeFailed()
        }
    }

    fun createImage(text: String?, w: Int, h: Int, logo: Bitmap?): Bitmap? {
        if (TextUtils.isEmpty(text)) {
            return null
        }
        try {
            val scaleLogo = getScaleLogo(logo, w, h)
            var offsetX = w / 2
            var offsetY = h / 2
            var scaleWidth = 0
            var scaleHeight = 0
            if (scaleLogo != null) {
                scaleWidth = scaleLogo.width
                scaleHeight = scaleLogo.height
                offsetX = (w - scaleWidth) / 2
                offsetY = (h - scaleHeight) / 2
            }
            val hints = Hashtable<EncodeHintType, Any?>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.MARGIN] = 0
            val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints)
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        var pixel = scaleLogo!!.getPixel(x - offsetX, y - offsetY)
                        if (pixel == 0) {
                            pixel = if (bitMatrix[x, y]) {
                                -0x1000000
                            } else {
                                -0x1
                            }
                        }
                        pixels[y * w + x] = pixel
                    } else {
                        if (bitMatrix[x, y]) {
                            pixels[y * w + x] = -0x1000000
                        } else {
                            pixels[y * w + x] = -0x1
                        }
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(
                w, h,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getScaleLogo(
        logo: Bitmap?,
        w: Int,
        h: Int
    ): Bitmap? {
        if (logo == null) return null
        val matrix = Matrix()
        val scaleFactor =
            Math.min(w * 1.0f / 5 / logo.width, h * 1.0f / 5 / logo.height)
        matrix.postScale(scaleFactor, scaleFactor)
        return Bitmap.createBitmap(logo, 0, 0, logo.width, logo.height, matrix, true)
    }

    fun setFragmentArgs(captureFragment: CaptureFragment?, layoutId: Int) {
        if (captureFragment == null || layoutId == -1) {
            return
        }
        val bundle = Bundle()
        bundle.putInt(LAYOUT_ID, layoutId)
        captureFragment.arguments = bundle
    }

    fun isLightEnable(isEnable: Boolean) {
        val camera = CameraManager.get().camera
        if (isEnable) {
            if (camera != null) {
                val parameter = camera.parameters
                parameter.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera.parameters = parameter
            }
        } else {
            if (camera != null) {
                val parameter = camera.parameters
                parameter.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera.parameters = parameter
            }
        }
    }

    interface AnalyzeCallback {
        fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?)
        fun onAnalyzeFailed()
    }
}
