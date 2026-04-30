package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordsDueForReviewUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    operator fun invoke(): Flow<List<Word>> =
        wordRepository.getWordsDueForReview()
}