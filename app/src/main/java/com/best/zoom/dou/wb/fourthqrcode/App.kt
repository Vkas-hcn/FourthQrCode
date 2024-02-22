package com.best.zoom.dou.wb.fourthqrcode

import android.app.Application
import com.best.zoom.dou.wb.fourthqrcode.utils.NetUtils
import java.util.UUID

class App : Application() {
    companion object {
        lateinit var instance: App
    }
    override fun onCreate() {
        instance = this
        super.onCreate()
        if(NetUtils.fqrcode_id.isEmpty()){
            NetUtils.fqrcode_id = UUID.randomUUID().toString()
        }
        NetUtils.getBlackList(this)
    }
}
