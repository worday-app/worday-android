package com.wordayapp.worday.domain.model

sealed class WordayError {
    data class Network(val code: Int? = null, val message: String = "") : WordayError()
    data class Database(val cause: Throwable) : WordayError()
    object NoInternet : WordayError()
    data class GeminiQuotaExceeded(val remaining: Int) : WordayError()
    object Unknown : WordayError()
}