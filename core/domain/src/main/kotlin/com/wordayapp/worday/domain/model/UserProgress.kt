package com.wordayapp.worday.domain.model

data class UserProgress(
    val userId: Int = 1,
    val level: WordLevel = WordLevel.A1,
    val dailyGoal: Int = 10,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long = 0L,
    val totalWordsLearned: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalAnswers: Int = 0,
) {
    val accuracyRate: Float
        get() = if (totalAnswers == 0) 0f
        else totalCorrectAnswers.toFloat() / totalAnswers.toFloat()

    companion object {
        fun createDefault() = UserProgress()
    }
}