package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityResultsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 1)
        val category = intent.getStringExtra("CATEGORY") ?: "General"

        displayResults(score, total)
        saveResults(score, total, category)
        setupButtons()
    }

    private fun displayResults(score: Int, total: Int) {
        binding.scoreText.text = getString(R.string.score_format, score, total)
        binding.correctAnswers.text = getString(R.string.correct_answers, score)
        binding.wrongAnswers.text = getString(R.string.wrong_answers, total - score)
        
        val percentage = (score.toFloat() / total.toFloat()) * 100
        binding.progressBar.progress = percentage.toInt()
        binding.percentageText.text = "%.1f%%".format(percentage)
    }

    private fun saveResults(score: Int, total: Int, category: String) {
        val userId = auth.currentUser?.uid ?: return
        val result = hashMapOf(
            "score" to score,
            "total" to total,
            "category" to category,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("results")
            .document(userId)
            .collection("quizzes")
            .add(result)
            .addOnSuccessListener {
                // Update leaderboard
                updateLeaderboard(userId, score, category)
            }
    }

    private fun updateLeaderboard(userId: String, score: Int, category: String) {
        db.collection("leaderboard")
            .document(category)
            .collection("scores")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val currentHighScore = document.getLong("score")?.toInt() ?: 0
                if (score > currentHighScore) {
                    document.reference.set(mapOf(
                        "score" to score,
                        "userId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    ))
                }
            }
    }

    private fun setupButtons() {
        binding.retryButton.setOnClickListener {
            finish()
        }

        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}