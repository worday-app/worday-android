package com.wordayapp.worday.domain.model

data class Word(
    val id: Int,
    val english: String,
    val turkish: String,
    val definition: String,
    val level: WordLevel,
    val type: WordType,
    val ipa: String,
    val exampleSentence1: String,
    val exampleSentence2: String,
    // SM-2 fields
    val repetitionCount: Int = 0,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val nextReviewDate: Long = 0L,
    // User state
    val isSaved: Boolean = false,
    val isLearned: Boolean = false,
)