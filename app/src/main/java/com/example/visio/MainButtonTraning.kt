package com.example.visio

import android.os.Bundle
import android.view.animation.Animation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.animation.ObjectAnimator
import android.widget.ImageButton
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.widget.TextView
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.core.content.ContextCompat
import android.content.SharedPreferences
import android.content.Context

class MainButtonTraning : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var exitCounter: Int = 0

    private lateinit var level_1: View
    private lateinit var level_2: View
    private lateinit var level_3: View
    private lateinit var level_4: View
    private lateinit var level_5: View

    private lateinit var dotView: ImageView
    private lateinit var text_level_button: TextView
    private lateinit var startButton: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_button_traning)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        sharedPreferences = getSharedPreferences("ExitCounterPrefs", Context.MODE_PRIVATE)

        // Загружаем счетчик выходов из SharedPreferences
        exitCounter = sharedPreferences.getInt("exitCounter", 0)

        fun exit() {
            incrementExitCounter()
            goToMain()
        }
        level_1 = findViewById(R.id.level_1)
        level_2 = findViewById(R.id.level_2)
        level_3 = findViewById(R.id.level_3)
        level_4 = findViewById(R.id.level_4)
        level_5 = findViewById(R.id.level_5)

        startButton = findViewById(R.id.next_level_button)
        text_level_button = findViewById(R.id.text_button_level)
        dotView = findViewById(R.id.dot_for_anim)
        startButton.visibility = View.GONE
        text_level_button.visibility = View.GONE

        // Запускаем первую анимацию
        startFirstAnimation()

        // Установка действий на кнопки для запуска новых анимаций
        startButton.setOnClickListener {
            when (startButton.contentDescription.toString()) {
                "Start Second Animation" -> startSecondAnimation()
                "Start Third Animation" -> startThirdAnimation()
                "Start Fourth Animation" -> startFourthAnimation()
                "Start Fifth Animation" -> startFifthAnimation()
                "Return to Main" -> exit()
            }
        }
    }

    private fun resetDotPosition() {
        dotView.translationX = 0f
        dotView.translationY = 0f
    }

    private fun startFirstAnimation() {
        resetDotPosition()

        // Первая анимация движения вправо и влево
        val firstAnimator = ObjectAnimator.ofFloat(dotView, "translationX", -300f, 300f)
        firstAnimator.duration = 1000
        firstAnimator.repeatMode = ObjectAnimator.REVERSE
        firstAnimator.repeatCount = ObjectAnimator.INFINITE // Бесконечная анимация

        firstAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                startButton.visibility = View.VISIBLE
                text_level_button.visibility = View.VISIBLE
                startButton.contentDescription = "Start Second Animation"
            }
        })

        firstAnimator.start()
    }

    private fun startSecondAnimation() {
        startButton.visibility = View.GONE
        text_level_button.visibility = View.GONE
        resetDotPosition()

        // Вторая анимация движения вверх и вниз
        val secondAnimator = ObjectAnimator.ofFloat(dotView, "translationY", -300f, 300f)
        secondAnimator.duration = 1000
        secondAnimator.repeatMode = ObjectAnimator.REVERSE
        secondAnimator.repeatCount = ObjectAnimator.INFINITE // Бесконечная анимация

        secondAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                startButton.visibility = View.VISIBLE
                text_level_button.visibility = View.VISIBLE
                startButton.contentDescription = "Start Third Animation"
            }
        })

        secondAnimator.start()
    }

    private fun startThirdAnimation() {
        startButton.visibility = View.GONE
        text_level_button.visibility = View.GONE
        resetDotPosition()

        // Третья анимация движения по диагонали
        val diagonalAnimator1X = ObjectAnimator.ofFloat(dotView, "translationX", -300f, 300f)
        val diagonalAnimator1Y = ObjectAnimator.ofFloat(dotView, "translationY", -300f, 300f)
        diagonalAnimator1X.duration = 1000
        diagonalAnimator1Y.duration = 1000
        diagonalAnimator1X.repeatMode = ObjectAnimator.REVERSE
        diagonalAnimator1Y.repeatMode = ObjectAnimator.REVERSE
        diagonalAnimator1X.repeatCount = ObjectAnimator.INFINITE
        diagonalAnimator1Y.repeatCount = ObjectAnimator.INFINITE

        diagonalAnimator1Y.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                startButton.visibility = View.VISIBLE
                text_level_button.visibility = View.VISIBLE
                startButton.contentDescription = "Start Fourth Animation"
            }
        })
        diagonalAnimator1X.start()
        diagonalAnimator1Y.start()
    }

    private fun startFourthAnimation() {
        startButton.visibility = View.GONE
        text_level_button.visibility = View.GONE
        resetDotPosition()

        // Анимация по кругу по часовой стрелке
        val rotateClockwiseX = ObjectAnimator.ofFloat(dotView, "translationX", 300f, 0f, -300f, 0f, 300f)
        val rotateClockwiseY = ObjectAnimator.ofFloat(dotView, "translationY", 0f, 300f, 0f, -300f, 0f)
        rotateClockwiseX.duration = 1000
        rotateClockwiseY.duration = 1000
        rotateClockwiseX.interpolator = LinearInterpolator()
        rotateClockwiseY.interpolator = LinearInterpolator()
        rotateClockwiseX.repeatCount = ObjectAnimator.INFINITE // Бесконечная анимация
        rotateClockwiseY.repeatCount = ObjectAnimator.INFINITE // Бесконечная анимация

        rotateClockwiseY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                startButton.visibility = View.VISIBLE
                text_level_button.visibility = View.VISIBLE
                startButton.contentDescription = "Start Fifth Animation"
            }
        })

        rotateClockwiseX.start()
        rotateClockwiseY.start()
    }

    private fun startFifthAnimation() {
        startButton.visibility = View.GONE
        text_level_button.visibility = View.GONE
        resetDotPosition()

        // Пятая анимация движения по форме восьмёрки
        val firstHalfFigureEightX = ObjectAnimator.ofFloat(dotView, "translationX", -300f, 300f)
        val firstHalfFigureEightY = ObjectAnimator.ofFloat(dotView, "translationY", 300f, -300f)
        firstHalfFigureEightX.duration = 1000
        firstHalfFigureEightY.duration = 1000
        firstHalfFigureEightX.repeatMode = ObjectAnimator.REVERSE
        firstHalfFigureEightY.repeatMode = ObjectAnimator.REVERSE
        firstHalfFigureEightX.repeatCount = ObjectAnimator.INFINITE
        firstHalfFigureEightY.repeatCount = ObjectAnimator.INFINITE

        firstHalfFigureEightY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                startButton.visibility = View.VISIBLE
                text_level_button.visibility = View.VISIBLE
                startButton.contentDescription = "Return to Main"
            }
        })

        firstHalfFigureEightX.start()
        firstHalfFigureEightY.start()
    }
    private fun incrementExitCounter() {
        exitCounter++
        val editor = sharedPreferences.edit()
        editor.putInt("exitCounter", exitCounter)
        editor.apply()
    }
    fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}