package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Initialize sample questions on first launch
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (!prefs.getBoolean("questions_initialized", false)) {
            QuestionInitializer.initializeSampleQuestions()
            prefs.edit().putBoolean("questions_initialized", true).apply()
        }

        setupCategoryButtons()
        setupNavigation()
    }

    private fun setupCategoryButtons() {
        binding.categoryScience.setOnClickListener { startQuiz("Science") }
        binding.categoryHistory.setOnClickListener { startQuiz("History") }
        binding.categoryMath.setOnClickListener { startQuiz("Math") }
        binding.categoryGeography.setOnClickListener { startQuiz("Geography") }
    }

    private fun startQuiz(category: String) {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("CATEGORY", category)
        }
        startActivity(intent)
    }

    private fun setupNavigation() {
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.leaderboardButton.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}