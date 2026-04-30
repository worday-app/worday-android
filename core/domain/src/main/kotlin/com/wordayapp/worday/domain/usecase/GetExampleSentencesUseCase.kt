package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.WordayError
import com.wordayapp.worday.domain.repository.GeminiRepository
import com.wordayapp.worday.domain.repository.GeminiResult
import javax.inject.Inject

class GetExampleSentencesUseCase @Inject constructor(
    private val geminiRepository: GeminiRepository,
) {
    suspend operator fun invoke(word: String): GeminiResult {
        val remaining = geminiRepository.getRemainingFreeRequests()
        if (remaining <= 0) {
            return GeminiResult.Error(WordayError.GeminiQuotaExceeded(remaining = 0))
        }
        val result = geminiRepository.getExampleSentences(word)
        if (result is GeminiResult.Success) {
            geminiRepository.consumeRequest()
        }
        return result
    }
}