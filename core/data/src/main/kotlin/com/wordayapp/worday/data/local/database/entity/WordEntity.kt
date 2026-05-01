package com.wordayapp.worday.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: Int,
    val english: String,
    val turkish: String,
    val definition: String,
    val ipa: String,
    val exampleSentence1: String,
    val exampleSentence2: String,
    val level: String,
    val type: String,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val repetitionCount: Int = 0,
    val isLearned: Boolean = false,
    val isSaved: Boolean = false,
    val nextReviewDate: Long = 0L
)