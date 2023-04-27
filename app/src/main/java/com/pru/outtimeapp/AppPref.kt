package com.pru.outtimeapp

import android.content.Context
import android.content.SharedPreferences

object AppPref :
    SharedPreferences by appContext.getSharedPreferences("app-preference", Context.MODE_PRIVATE) {

    var startTime: Long
        set(value) {
            edit().putLong("StartTime", value).apply()
        }
        get() = getLong("StartTime", 0L)

    var endTime: Long
        set(value) {
            edit().putLong("EndTime", value).apply()
        }
        get() = getLong("EndTime", 0L)

    var timerTime: Long
        set(value) {
            edit().putLong("TimerTime", value).apply()
        }
        get() = getLong("TimerTime", 0L)
}