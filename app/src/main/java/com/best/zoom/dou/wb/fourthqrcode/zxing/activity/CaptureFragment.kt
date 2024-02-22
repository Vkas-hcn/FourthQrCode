package com.best.zoom.dou.wb.fourthqrcode.zxing.activity

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.best.zoom.dou.wb.fourthqrcode.R
import com.best.zoom.dou.wb.fourthqrcode.zxing.activity.CodeUtils.AnalyzeCallback
import com.best.zoom.dou.wb.fourthqrcode.zxing.camera.CameraManager
import com.best.zoom.dou.wb.fourthqrcode.zxing.decoding.CaptureActivityHandler
import com.best.zoom.dou.wb.fourthqrcode.zxing.decoding.InactivityTimer
import com.best.zoom.dou.wb.fourthqrcode.zxing.view.ViewfinderView
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import java.util.Vector

class CaptureFragment : Fragment(), SurfaceHolder.Callback {
    private var handler: CaptureActivityHandler? = null
    private var viewfinderView: ViewfinderView? = null
    private var hasSurface = false
    private var decodeFormats: Vector<BarcodeFormat>? = null
    private var characterSet: String? = null
    private var inactivityTimer: InactivityTimer? = null
    private val mediaPlayer: MediaPlayer? = null
    private var playBeep = false
    private var vibrate = false
    private var surfaceView: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null
    var analyzeCallback: AnalyzeCallback? = null
    private var camera: Camera? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CameraManager.init(activity?.application)
        hasSurface = false
        inactivityTimer = InactivityTimer(this.activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bundle = arguments
        var view: View? = null
        if (bundle != null) {
            val layoutId = bundle.getInt(CodeUtils.LAYOUT_ID)
            if (layoutId != -1) {
                view = inflater.inflate(layoutId, null)
            }
        }
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_capture, null)
        }
        viewfinderView = view!!.findViewById<View>(R.id.viewfinder_view) as ViewfinderView
        surfaceView = view.findViewById<View>(R.id.preview_view) as SurfaceView
        surfaceHolder = surfaceView!!.holder
        return view
    }

    override fun onResume() {
        super.onResume()
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            surfaceHolder!!.addCallback(this)
            surfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
        decodeFormats = null
        characterSet = null
        playBeep = true
        val audioService = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false
        }
        vibrate = true
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        super.onDestroy()
        inactivityTimer!!.shutdown()
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    fun handleDecode(result: Result?, barcode: Bitmap?) {
        inactivityTimer!!.onActivity()
        if (result == null || TextUtils.isEmpty(result.text)) {
            if (analyzeCallback != null) {
                analyzeCallback!!.onAnalyzeFailed()
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback!!.onAnalyzeSuccess(barcode, result.text)
            }
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        camera = try {
            CameraManager.get().openDriver(surfaceHolder)
            CameraManager.get().camera
        } catch (e: Exception) {
            if (callBack != null) {
                callBack!!.callBack(e)
            }
            return
        }
        if (callBack != null) {
            callBack!!.callBack(null)
        }
        if (handler == null) {
            handler = CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView)
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
        if (camera != null) {
            if (camera != null && CameraManager.get().isPreviewing) {
                if (!CameraManager.get().isUseOneShotPreviewCallback) {
                    camera!!.setPreviewCallback(null)
                }
                camera!!.stopPreview()
                CameraManager.get().previewCallback.setHandler(null, 0)
                CameraManager.get().autoFocusCallback.setHandler(null, 0)
                CameraManager.get().isPreviewing = false
            }
        }
    }

    fun getHandler(): Handler? {
        return handler
    }

    fun drawViewfinder() {
        viewfinderView!!.drawViewfinder()
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private val beepListener = OnCompletionListener { mediaPlayer -> mediaPlayer.seekTo(0) }
    var callBack: CameraInitCallBack? = null

    /**
     * Set callback for Camera check whether Camera init success or not.
     */
    fun setCameraInitCallBack(callBack: CameraInitCallBack?) {
        this.callBack = callBack
    }

    interface CameraInitCallBack {
        /**
         * Callback for Camera init result.
         * @param e If is's null,means success.otherwise Camera init failed with the Exception.
         */
        fun callBack(e: Exception?)
    }

    companion object {
        private const val BEEP_VOLUME = 0.10f
    }
}
