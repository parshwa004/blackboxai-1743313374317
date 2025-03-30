package com.example.quizapp.models

data class Question(
    val id: String = "",
    val text: String = "",
    val options: List<String> = listOf(),
    val correctAnswer: String = "",
    val difficulty: String = "medium",
    val category: String = "general",
    val explanation: String = ""
) {
    // Helper function to convert to map for Firestore
    fun toMap(): Map<String, Any> {
        return mapOf(
            "text" to text,
            "options" to options,
            "correctAnswer" to correctAnswer,
            "difficulty" to difficulty,
            "category" to category,
            "explanation" to explanation
        )
    }
}