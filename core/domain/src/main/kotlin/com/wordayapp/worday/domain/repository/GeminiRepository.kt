package com.wordayapp.worday.domain.repository

import com.wordayapp.worday.domain.model.WordayError

sealed class GeminiResult {
    data class Success(val sentences: List<String>) : GeminiResult()
    data class Error(val error: WordayError) : GeminiResult()
}

interface GeminiRepository {
    suspend fun getExampleSentences(word: String, count: Int = 3): GeminiResult
    suspend fun getRemainingFreeRequests(): Int
    suspend fun consumeRequest()
}