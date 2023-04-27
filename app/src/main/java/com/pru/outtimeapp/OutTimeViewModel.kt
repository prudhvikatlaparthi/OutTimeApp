package com.pru.outtimeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OutTimeViewModel : ViewModel() {
    private val _currentTime = MutableStateFlow<Calendar>(Calendar.getInstance())
    val currentTime get() = _currentTime.asStateFlow()
    var signInTime = if (AppPref.startTime == 0L) null else Calendar.getInstance().apply {
        timeInMillis = AppPref.startTime
    }
    private val _differTracker = MutableStateFlow("")
    val differTracker get() = _differTracker.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    init {
        viewModelScope.launch {
            while (true) {
                delay(900)
                _currentTime.value = Calendar.getInstance()
            }
        }
        if (signInTime != null) {
            initializeCounter()
            countDownTimer!!.start()
        }
    }

    private fun getMillis(startTime: Calendar?): Long {
        return if (startTime != null) {
            val cTime = Calendar.getInstance()
            val endTime = getEndTime(startTime)
            val diff = endTime.timeInMillis - cTime.timeInMillis
            diff
        } else AppPref.timerTime
    }

    fun getTime(selectedTime: Calendar): String {
        return SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(selectedTime.time)
    }

    fun getEndTime(selectedTime: Calendar): Calendar {
        val endTime = selectedTime.clone() as Calendar
        val hour = AppPref.timerTime / 3600000 % 24
        val min = AppPref.timerTime / 60000 % 60
        endTime.add(Calendar.HOUR, hour.toInt())
        endTime.add(Calendar.MINUTE, min.toInt())
        return endTime
    }

    fun signIn() {
        signInTime = _currentTime.value
        AppPref.startTime = signInTime!!.timeInMillis
        initializeCounter()
        countDownTimer?.start()

        val intent = Intent(mainActivity, MainActivity::class.java)
        intent.putExtra("FromNotification", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            mainActivity,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        (mainActivity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(
            AlarmManager.RTC,
            getEndTime(signInTime!!).timeInMillis,
            pendingIntent
        )
    }

    fun signOut() {
        signInTime = null
        AppPref.startTime = 0
        _differTracker.value = ""
        countDownTimer?.cancel()
        countDownTimer = null
    }


    private fun initializeCounter() {
        countDownTimer = object : CountDownTimer(getMillis(signInTime), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val hour = millisUntilFinished / 3600000 % 24
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                _differTracker.value =
                    f.format(hour) + ":" + f.format(min) + ":" + f.format(sec)
            }

            override fun onFinish() {
                _differTracker.value = "00:00:00"
            }
        }
    }

}