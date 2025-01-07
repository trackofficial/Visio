package com.example.visio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log

class DailyResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailyResetReceiver", "onReceive: started")

        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val dayCounter = sharedPreferences.getInt("dayCounter", 0) + 1

        val editor = sharedPreferences.edit()
        editor.putInt("dayCounter", dayCounter)
        editor.apply()

        Log.d("DailyResetReceiver", "Day counter updated: $dayCounter")
    }
}