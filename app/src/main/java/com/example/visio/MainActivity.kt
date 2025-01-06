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
import android.media.Image
import android.util.TypedValue
import android.widget.ImageButton
import android.util.Log
class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var exitCounterTextView: TextView
    private lateinit var navigateButton: ImageButton
    private lateinit var weekStatistic: LinearLayout
    private lateinit var daysOfWeek: List<DayOfWeek>
    private lateinit var resetHandler: PendingIntent
    private var currentDayOfWeek: DayOfWeek? = null

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
        navigateButton = findViewById(R.id.logo_button_main)
        weekStatistic = findViewById(R.id.week_statistic)

        // Инициализация объектов для каждого дня недели
        daysOfWeek = listOf(
            DayOfWeek(Calendar.SUNDAY, "Воскресенье", R.id.sunday_bar, R.id.sunday_value),
            DayOfWeek(Calendar.MONDAY, "Понедельник", R.id.monday_bar, R.id.monday_value),
            DayOfWeek(Calendar.TUESDAY, "Вторник", R.id.tuesday_bar, R.id.tuesday_value),
            DayOfWeek(Calendar.WEDNESDAY, "Среда", R.id.wensday_bar, R.id.wensday_value),
            DayOfWeek(Calendar.THURSDAY, "Четверг", R.id.thursday_bar, R.id.thursday_value),
            DayOfWeek(Calendar.FRIDAY, "Пятница", R.id.friday_bar, R.id.friday_value),
            DayOfWeek(Calendar.SATURDAY, "Суббота", R.id.saturday_bar, R.id.saturday_value)
        )

        // Проверка на сброс данных
        // Проверка на сброс данных
        checkForReset()

        // Загружаем данные текущего дня
        loadDayData()

        // Устанавливаем клик слушатель на кнопку
        navigateButton.setOnClickListener {
            handler.postDelayed({
                incrementExitCount()
                saveDayData()
                updateExitCounterTextView()
                updateBarHeight(currentDayOfWeek!!)},2000)
            val intent = Intent(this, MainButtonTraning::class.java)
            startActivity(intent)
        }

        // Установка ежедневного сброса в 00:00
        setDailyReset()

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

    private fun loadDayData() {
        daysOfWeek.forEach { day ->
            day.exitCount = sharedPreferences.getInt("day_${day.id}", 0)
            updateBarHeight(day)
            updateExitCountText(day)
        }
        val calendar = Calendar.getInstance()
        currentDayOfWeek = daysOfWeek.find { it.id == calendar.get(Calendar.DAY_OF_WEEK) }
        updateExitCounterTextView()
    }

    private fun saveDayData() {
        val editor = sharedPreferences.edit()
        currentDayOfWeek?.let {
            editor.putInt("day_${it.id}", it.exitCount)
        }
        editor.apply()
    }

    private fun incrementExitCount() {
        currentDayOfWeek?.exitCount = currentDayOfWeek?.exitCount?.plus(1) ?: 0
    }

    private fun updateExitCounterTextView() {
        exitCounterTextView.text = "${currentDayOfWeek?.exitCount}/0"
    }

    private fun updateBarHeight(day: DayOfWeek) {
        val barView = findViewById<View>(day.barViewId)
        barView.layoutParams.width = dpToPx(day.exitCount * 15) // 20dp за каждую прибавку
        barView.requestLayout()
    }

    private fun updateExitCountText(day: DayOfWeek) {
        val countTextView = findViewById<TextView>(day.countViewId)
        countTextView.text = day.exitCount.toString()
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
    private fun checkForReset() {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = currentTime }
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val lastResetDay = sharedPreferences.getInt("lastResetDay", currentDay)

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && currentDay != lastResetDay) {
            // Сбрасываем счетчики выходов для всех дней недели
            val editor = sharedPreferences.edit()
            for (day in daysOfWeek) {
                day.exitCount = 0
                editor.putInt("day_${day.id}", 0)
            }
            editor.putInt("lastResetDay", currentDay)
            editor.apply()
        } else if (currentDay != lastResetDay) {
            // Сбрасываем только счетчик текущего дня недели
            currentDayOfWeek?.let {
                val editor = sharedPreferences.edit()
                it.exitCount = 0
                editor.putInt("day_${it.id}", 0)
                editor.putInt("lastResetDay", currentDay)
                editor.apply()
            }
        }
    }

    private fun setDailyReset() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MainActivity::class.java)
        resetHandler = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Если текущее время уже после 00:00, установить сброс на следующий день
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            resetHandler
        )
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
}