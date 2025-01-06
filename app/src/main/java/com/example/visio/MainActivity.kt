package com.example.visio

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import android.app.AlarmManager
import android.app.PendingIntent
import android.util.TypedValue
class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var exitCounterTextView: TextView
    private var exitCounter: Int = 0
    private var lastResetTime: Long = 0

    private lateinit var dayTextView: TextView
    private var dayCount: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var ratingBar: RatingBar
    private lateinit var text_rating: TextView
    private lateinit var block_rating: LinearLayout
    private lateinit var week_statistic: LinearLayout
    private var updateDayRunnable = object : Runnable {
        override fun run() {
            updateDay()
            handler.postDelayed(this, getMillisUntilMidnight())
        }
    }
    private val preferences by lazy { getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        sharedPreferences = getSharedPreferences("ExitCounterPrefs", Context.MODE_PRIVATE)
        exitCounterTextView = findViewById(R.id.training_counter_text)

        // Загружаем счетчик выходов и время последнего сброса из SharedPreferences
        exitCounter = sharedPreferences.getInt("exitCounter", 0)
        lastResetTime = sharedPreferences.getLong("lastResetTime", 0)

        // Проверяем, прошла ли полночь
        checkForReset()

        // Обновляем TextView
        updateExitCounterTextView()


        dayTextView = findViewById(R.id.day_counter)
        block_rating = findViewById(R.id.rating_your_eyes)
        dayTextView.text = "Day $dayCount"
        handler.post(updateDayRunnable)
        ratingBar = findViewById(R.id.ratingbar)
        text_rating = findViewById(R.id.text_rating_bar)
        week_statistic = findViewById(R.id.week_statistic)
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating > 0) {
                preferences.edit().putInt("rating", rating.toInt()).apply()
                preferences.edit().putLong("last_rating_time", System.currentTimeMillis()).apply()
                // Плавное скрытие RatingBar
                ratingBar.animate()
                    .alpha(0f)
                    .setDuration(400) // Продолжительность анимации в миллисекундах
                    .withEndAction {
                        ratingBar.visibility = View.GONE
                    }
                // Запланировать повторное отображение RatingBar в начале следующего дня
                handler.postDelayed({
                    showRatingBar()
                }, getMillisUntilMidnight())
            }
            if (rating >=3) {
                preferences.edit().putInt("rating", rating.toInt()).apply()
                preferences.edit().putLong("last_rating_time", System.currentTimeMillis()).apply()
                text_rating.text = "Рады это слышать :)"
                handler.postDelayed({
                    showRatingBar()
                }, getMillisUntilMidnight())
            }
            if (rating <= 2) {
                preferences.edit().putInt("rating", rating.toInt()).apply()
                preferences.edit().putLong("last_rating_time", System.currentTimeMillis()).apply()
                text_rating.text = "Печально это слышать :("
                handler.postDelayed({
                    showRatingBar()
                }, getMillisUntilMidnight())
            }
        }
        checkRatingStatus()
    }
    private fun showRatingBar() {
        runOnUiThread {
            ratingBar.visibility = View.VISIBLE
            ratingBar.alpha = 0f
            ratingBar.animate()
                .alpha(0f)
                .setDuration(400) // Продолжительность анимации появления в миллисекундах

        }
    }
    fun gotoCountSettings(view: View){
        val intent = Intent(this, SettingsTrainCount::class.java)
        startActivity(intent)
    }
    fun gotoSecondActivity(view: View){
        val intent = Intent(this, MainButtonTraning::class.java)
        startActivity(intent)
    }
    fun gotoShop(view: View){
        val intent = Intent(this, Shop::class.java)
        startActivity(intent)
    }
    fun gotoSettings(view: View){
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
    fun gotoAccount(view: View){
        val intent = Intent(this, Account::class.java)
        startActivity(intent)
    }

        private fun updateDay() {
            dayCount++
            dayTextView.text = "Day $dayCount"
        }

        private fun getMillisUntilMidnight(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            return calendar.timeInMillis - System.currentTimeMillis()
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(updateDayRunnable)
        }

    private fun checkRatingStatus() {
        val lastRatingTime = preferences.getLong("last_rating_time", 0)
        if (System.currentTimeMillis() - lastRatingTime < getMillisUntilMidnight()) {
            ratingBar.visibility = View.GONE
            val rating = preferences.getInt("rating", 0)
            if (rating >= 3) {
                text_rating.text = "Рады это слышать :)"
            } else {
                text_rating.text = "Печально это слышать :("
            }
            block_rating.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Получение счетчика выходов из AnimationActivity
            val dayOfWeek = data?.getIntExtra("dayOfWeek", Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            if (dayOfWeek != null) {
                exitCounter = data.getIntExtra("exitCounter", 0)
                updateExitCounterTextView()

                // Сохранение счетчика выходов для текущего дня недели в SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putInt("day_$dayOfWeek", exitCounter)
                editor.apply()
            }
        }
    }

    private fun updateExitCounterTextView() {
        exitCounterTextView.text = "$exitCounter/0"
    }

    private fun checkForReset() {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)

        val lastResetDay = sharedPreferences.getInt("lastResetDay", currentDay)

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && currentDay != lastResetDay) {
            // Сбрасываем счетчики выходов для всех дней недели
            val editor = sharedPreferences.edit()
            for (day in Calendar.SUNDAY..Calendar.SATURDAY) {
                editor.putInt("day_$day", 0)
            }
            editor.putInt("lastResetDay", currentDay)
            editor.apply()
        }
    }
}