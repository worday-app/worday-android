package com.wordayapp.worday.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_sessions")
data class DailySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val wordsStudied: Int = 0,
    val correctAnswers: Int = 0,
    val totalAnswers: Int = 0,
    val isCompleted: Boolean = false
)