package com.pru.outtimeapp

import android.app.Application
import android.content.Context
import android.provider.Settings
import com.pru.outtimeapp.utils.Global

private var _appContext: Context? = null
val appContext: Context
    get() = _appContext ?: throw IllegalStateException("appContext not initialized")

var mainActivity: MainActivity? by Global.weakReference(null)

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        _appContext = this
//        AppPref.timerTime = 34199617
        AppPref.timerTime = 60000
    }
}