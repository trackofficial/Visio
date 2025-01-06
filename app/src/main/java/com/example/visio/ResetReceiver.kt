package com.example.visio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.*

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ResetReceiver : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetCounter()
        finish() // Завершаем активность сразу после сброса счетчика
    }

    private fun resetCounter() {
        val sharedPreferences = getSharedPreferences("ExitCounterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("exitCounter", 0)
        editor.apply()
    }
}