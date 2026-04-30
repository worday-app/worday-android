package com.wordayapp.worday.domain.model

data class DailySession(
    val id: Int = 0,
    val date: Long,
    val wordsStudied: Int = 0,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val isCompleted: Boolean = false,
) {
    val successRate: Float
        get() = if (totalAnswers == 0) 0f
        else correctAnswers.toFloat() / totalAnswers.toFloat()
}