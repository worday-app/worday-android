package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.repository.WordRepository
import javax.inject.Inject

class GetLevelTestWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(): List<Word> =
        wordRepository.getLevelTestWords()
}