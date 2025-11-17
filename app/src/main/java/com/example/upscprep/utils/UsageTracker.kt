package com.example.upscprep.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UsageTracker(private val context: Context) {
    private val prefs = context.getSharedPreferences("usage_prefs", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun onAppStart() {
        // No-op for now
    }

    fun addStudyDuration(millis: Long) {
        val todayKey = todayKey()
        val prev = prefs.getLong("study_$todayKey", 0L)
        prefs.edit().putLong("study_$todayKey", prev + millis).apply()
        updateStreak()
    }

    fun getTodayStudyMillis(): Long {
        return prefs.getLong("study_${todayKey()}", 0L)
    }

    fun getCurrentStreak(): Int {
        return prefs.getInt("streak", 0)
    }

    private fun updateStreak() {
        val todayStr = todayKey()
        val last = prefs.getString("last_date", null)
        val currentStreak = prefs.getInt("streak", 0)

        if (last == null) {
            prefs.edit().putString("last_date", todayStr).putInt("streak", 1).apply()
            return
        }
        val lastCal = Calendar.getInstance().apply { time = dateFormat.parse(last) ?: time }
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        val lastStr = dateFormat.format(lastCal.time)
        val todayOnly = dateFormat.format(todayCal.time)
        val yesterdayOnly = dateFormat.format(yesterdayCal.time)

        when (lastStr) {
            todayOnly -> return
            yesterdayOnly -> prefs.edit().putString("last_date", todayStr).putInt("streak", currentStreak + 1).apply()
            else -> prefs.edit().putString("last_date", todayStr).putInt("streak", 1).apply()
        }
    }

    private fun todayKey(): String {
        return dateFormat.format(Calendar.getInstance().time)
    }
}
