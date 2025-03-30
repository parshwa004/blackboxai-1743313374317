package com.example.quizapp.utils

import com.example.quizapp.models.Question
import com.google.firebase.firestore.FirebaseFirestore

object QuestionInitializer {
    fun initializeSampleQuestions() {
        val db = FirebaseFirestore.getInstance()
        
        val scienceQuestions = listOf(
            Question(
                text = "What is the chemical symbol for water?",
                options = listOf("H2O", "CO2", "NaCl", "O2"),
                correctAnswer = "H2O",
                difficulty = "easy",
                category = "Science"
            ),
            Question(
                text = "Which planet is known as the Red Planet?",
                options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
                correctAnswer = "Mars",
                difficulty = "easy",
                category = "Science"
            ),
            Question(
                text = "What is the powerhouse of the cell?",
                options = listOf("Nucleus", "Mitochondria", "Ribosome", "Golgi Apparatus"),
                correctAnswer = "Mitochondria",
                difficulty = "medium",
                category = "Science"
            )
        )

        val historyQuestions = listOf(
            Question(
                text = "In which year did World War II end?",
                options = listOf("1943", "1945", "1947", "1950"),
                correctAnswer = "1945",
                difficulty = "medium",
                category = "History"
            ),
            Question(
                text = "Who was the first president of the United States?",
                options = listOf("Thomas Jefferson", "John Adams", "George Washington", "Abraham Lincoln"),
                correctAnswer = "George Washington",
                difficulty = "easy",
                category = "History"
            )
        )

        val mathQuestions = listOf(
            Question(
                text = "What is 2 + 2?",
                options = listOf("3", "4", "5", "6"),
                correctAnswer = "4",
                difficulty = "easy",
                category = "Math"
            ),
            Question(
                text = "What is the value of π (pi) to two decimal places?",
                options = listOf("3.14", "3.16", "3.18", "3.20"),
                correctAnswer = "3.14",
                difficulty = "easy",
                category = "Math"
            )
        )

        val geographyQuestions = listOf(
            Question(
                text = "What is the capital of France?",
                options = listOf("London", "Berlin", "Paris", "Madrid"),
                correctAnswer = "Paris",
                difficulty = "easy",
                category = "Geography"
            ),
            Question(
                text = "Which country has the largest population?",
                options = listOf("India", "United States", "China", "Indonesia"),
                correctAnswer = "China",
                difficulty = "medium",
                category = "Geography"
            )
        )

        // Add questions to Firestore
        addQuestionsToCategory("Science", scienceQuestions)
        addQuestionsToCategory("History", historyQuestions)
        addQuestionsToCategory("Math", mathQuestions)
        addQuestionsToCategory("Geography", geographyQuestions)
    }

    private fun addQuestionsToCategory(category: String, questions: List<Question>) {
        val db = FirebaseFirestore.getInstance()
        questions.forEach { question ->
            db.collection("quizzes")
                .document(category)
                .collection("questions")
                .add(question.toMap())
        }
    }
}