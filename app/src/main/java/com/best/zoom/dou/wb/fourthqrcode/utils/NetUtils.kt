package com.best.zoom.dou.wb.fourthqrcode.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.best.zoom.dou.wb.fourthqrcode.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

object NetUtils {
    const val BLACK_URL = "https://africa.smallstillqr.com/call/eta/shank"
    private val sharedPreferences by lazy {
        App.instance.getSharedPreferences(
            "fqrcode",
            Context.MODE_PRIVATE
        )
    }
    var fqrcode_id = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("fqrcode_id", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("fqrcode_id", "").toString()

    var fqrcode_black_key = ""
        set(value) {
            sharedPreferences.edit().run {
                putString("fqrcode_black_value", value)
                commit()
            }
            field = value
        }
        get() = sharedPreferences.getString("fqrcode_black_value", "").toString()
    private fun cloakMapData(context: Context): Map<String, Any> {
        return mapOf<String, Any>(
            //distinct_id
            "carcass" to (fqrcode_id),
            //client_ts
            "blanc" to (System.currentTimeMillis()),
            //device_model
            "mauricio" to Build.MODEL,
            //bundle_id
            "absurd" to "com.small.still.qr.scan.mini",
            //os_version
            "hush" to Build.VERSION.RELEASE,
            //gaid
            "san" to "",
            //android_id
            "warsaw" to context.getAppId(),
            //os
            "forgot" to "fledge",
            //app_version
            "peace" to context.getAppVersion(),
        )
    }

    private fun Context.getAppVersion(): String {
        try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }

    private fun Context.getAppId(): String {
        return Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getBlackList(context: Context) {
        if (fqrcode_black_key.isNotEmpty()) {
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            getMapData(BLACK_URL,cloakMapData(context), onNext = {
                Log.e("TAG", "The blacklist request is successful：$it")
                fqrcode_black_key = it
            }, onError = {
                retry(it,context)
            })
        }
    }

    private fun retry(it: String,context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(10021)
            Log.e("TAG", "The blacklist request failed：$it")
            getBlackList(context)
        }
    }

    private fun getMapData(url: String, map: Map<String, Any>, onNext: (response: String) -> Unit, onError: (error: String) -> Unit) {
        val queryParameters = StringBuilder()
        for ((key, value) in map) {
            if (queryParameters.isNotEmpty()) {
                queryParameters.append("&")
            }
            queryParameters.append(URLEncoder.encode(key, "UTF-8"))
            queryParameters.append("=")
            queryParameters.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }

        val urlString = if (url.contains("?")) {
            "$url&$queryParameters"
        } else {
            "$url?$queryParameters"
        }
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000 // 设置连接超时时间
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                inputStream.close()
                onNext(response.toString())
            } else {
                onError("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            onError("Network error: ${e.message}")
        } finally {
            connection.disconnect()
        }
    }
}