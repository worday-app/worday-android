package com.wordayapp.worday.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: Int = 1,
    val level: String = "A1",
    val dailyGoal: Int = 10,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long = 0L,
    val totalWordsLearned: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalAnswers: Int = 0
)