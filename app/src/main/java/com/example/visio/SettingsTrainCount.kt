package com.example.visio

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.TextView
import android.content.SharedPreferences

class SettingsTrainCount : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var counterText: TextView
    private lateinit var decrementButton: ImageButton
    private lateinit var incrementButton: ImageButton
    private var counter: Int = 0
    private var maxCounter: Int = 10  // Предположим начальное максимальное значение 10
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settins_for_counter_train)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_settings_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("CounterActivity", "onCreate: started")
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        counterText = findViewById(R.id.counter_value)
        decrementButton = findViewById(R.id.decrementButton)
        incrementButton = findViewById(R.id.incrementButton)
        Log.d("CounterActivity", "Views initialized")

        // Загружаем счётчик из SharedPreferences
        counter = sharedPreferences.getInt("counter", 0)
        updateCounterText()
        Log.d("CounterActivity", "Counter loaded: $counter")

        // Обработчики кнопок для управления значением счётчика
        decrementButton.setOnClickListener {
            if (counter > 0) {
                counter--
                updateCounterText()
                saveCounter()
                Log.d("CounterActivity", "Decremented counter: $counter")
            }
        }
        incrementButton.setOnClickListener {
            counter++
            updateCounterText()
            saveCounter()
            Log.d("CounterActivity", "Incremented counter: $counter")
        }

        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            gotomain()
        }

        Log.d("CounterActivity", "onCreate: ended")
    }

    fun gotomain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun updateCounterText() {
        counterText.text = counter.toString()
        Log.d("CounterActivity", "updateCounterText: counter=$counter")
    }

    private fun saveCounter() {
        val editor = sharedPreferences.edit()
        editor.putInt("counter", counter)
        editor.apply()
        Log.d("CounterActivity", "saveCounter: counter=$counter")
    }
}