package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityQuizBinding
import com.google.firebase.firestore.FirebaseFirestore

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var timer: CountDownTimer
    private lateinit var category: String
    private var currentQuestionIndex = 0
    private var score = 0
    private var questions = listOf<Question>()
    private var timeLeftInMillis = 30000 // 30 seconds per question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        category = intent.getStringExtra("CATEGORY") ?: "General"

        setupUI()
        loadQuestions()
    }

    private fun setupUI() {
        binding.categoryText.text = category
        binding.nextButton.setOnClickListener { goToNextQuestion() }
        binding.submitButton.setOnClickListener { checkAnswer() }
    }

    private fun loadQuestions() {
        db.collection("quizzes")
            .document(category)
            .collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                questions = documents.toObjects(Question::class.java)
                if (questions.isNotEmpty()) {
                    showQuestion(currentQuestionIndex)
                    startTimer()
                } else {
                    finishWithMessage("No questions available for this category")
                }
            }
            .addOnFailureListener {
                finishWithMessage("Failed to load questions")
            }
    }

    private fun showQuestion(index: Int) {
        if (index >= questions.size) {
            finishQuiz()
            return
        }

        val question = questions[index]
        binding.questionText.text = question.text
        binding.questionNumber.text = getString(R.string.question_format, index + 1, questions.size)
        
        // Clear previous selections
        binding.optionsGroup.clearCheck()
        
        // Set up options
        binding.option1.text = question.options[0]
        binding.option2.text = question.options[1]
        binding.option3.text = question.options[2]
        binding.option4.text = question.options[3]
    }

    private fun checkAnswer() {
        val selectedId = binding.optionsGroup.checkedRadioButtonId
        if (selectedId == -1) {
            binding.optionsGroup.error = getString(R.string.error_select_answer)
            return
        }

        val selectedOption = findViewById<RadioButton>(selectedId)
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        val isCorrect = selectedOption.text.toString() == correctAnswer

        if (isCorrect) {
            score++
            binding.scoreText.text = getString(R.string.score_format, score, questions.size)
        }

        // Disable options after submission
        binding.optionsGroup.isEnabled = false
        binding.submitButton.isEnabled = false
    }

    private fun goToNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            showQuestion(currentQuestionIndex)
            resetTimer()
            binding.optionsGroup.isEnabled = true
            binding.submitButton.isEnabled = true
        } else {
            finishQuiz()
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished.toInt()
                binding.timerText.text = getString(R.string.time_remaining, timeLeftInMillis / 1000)
            }

            override fun onFinish() {
                goToNextQuestion()
            }
        }.start()
    }

    private fun resetTimer() {
        timer.cancel()
        timeLeftInMillis = 30000
        startTimer()
    }

    private fun finishQuiz() {
        timer.cancel()
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("SCORE", score)
            putExtra("TOTAL", questions.size)
            putExtra("CATEGORY", category)
        }
        startActivity(intent)
        finish()
    }

    private fun finishWithMessage(message: String) {
        // Show error message and return to main screen
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}