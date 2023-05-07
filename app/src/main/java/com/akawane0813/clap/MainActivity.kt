package com.akawane0813.clap

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf

/**

The MainActivity class represents the main screen of the app that contains a quiz game.
This class initializes the views, sound effects and questions, and allows the user to play the
quiz game by providing answers. It also tracks the user's score and displays the score when the
app is closed.

@constructor Creates an instance of the MainActivity class.
 */

class MainActivity : AppCompatActivity() {

    private var points = 0
    private var currentQuestion: String? = null
    private var currentAnswer: String? = null

    private var totalQuestion = 0
    private var soundPool: SoundPool? = null
    private var successSound = -1
    private var failureSound = -1

    private var imgAnimation: ImageView? = null
    private var textView1: TextView? = null
    private var response: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgAnimation = findViewById(R.id.imgClap)
        textView1 = findViewById(R.id.textView1)
        response = findViewById(R.id.response_edit_text)
        initialize()
    }

    fun initialize() {
        assignQuestion()
        resetView()
        initializeSound()
    }

    /**
     * Initializes success/failure sound
     */
    fun initializeSound() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        successSound =  soundPool!!.load(baseContext, R.raw.correct, 1)
        failureSound =  soundPool!!.load(baseContext, R.raw.wrong, 1)

    }

    /**
     * Quiz trivia question set
     */
    fun assignQuestion() {
        // Define a list of questions and answers
        val questions = listOf(
            "What is the capital of France?",
            "What is the capital of India?",
            "What is the capital of United States?",
            "What is the capital of United Kingdom?",
            "What is the capital of Germany?",
            "What is the capital of Japan?",
            "What is the capital of Italy?"
        )
        val answers = listOf(
            "Paris",
            "New Delhi",
            "Washington D.C",
            "London",
            "Berlin",
            "Tokyo",
            "Rome"
        )

        // Randomly select a question and its corresponding answer
        val index = (0 until questions.size).shuffled().first()
        currentQuestion = questions[index]
        currentAnswer = answers[index]
    }

    /**
     * Check answer and show animation accordingly
     */
    fun calculateClick(view: View) {
        val response = response?.text.toString()

        if(TextUtils.isEmpty(response)) {
            return;
        }

        if(response == currentAnswer)
            correctResult()
        else
            wrongAnswer()
    }

    /**
     * Rreset view of the application after every attempt
     */
    fun resetView() {
        response?.setText("")
        textView1?.setText(currentQuestion)
    }

    /**
     * Plays success sound and animation on the correct answer
     */
    fun correctResult() {
        points++
        totalQuestion++

        //play sound
        soundPool?.play(successSound, 1F, 1F, 0, 0, 1F)

        //animate
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.animate)
        imgAnimation?.setBackgroundResource(R.drawable.correct);
        imgAnimation?.startAnimation(anim)


        assignQuestion()
        resetView()
    }

    /**
     * Plays sound and animation on the wrong answer
     */
    fun wrongAnswer() {
        assignQuestion()
        resetView()
        totalQuestion++

        soundPool?.play(failureSound, 1F, 1F, 0, 0, 1F)

        //animate
        val anim: Animation = AnimationUtils.loadAnimation(this, R.anim.animate)
        imgAnimation?.setBackgroundResource(R.drawable.angry);
        imgAnimation?.startAnimation(anim)

    }

    /**
     * This function will calculate the score using WorkManager
     * and displays in the notification.
     */
    override fun onStop() {
        super.onStop()

        val message = "" + points + " correct answers out of " + totalQuestion
        // Start the Worker if the timer is running
        val timerWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInputData(
                workDataOf(
                    KEY_SCORE to message
                )
            ).build()

        WorkManager.getInstance(applicationContext).enqueue(timerWorkRequest)
    }
}

