package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    operator fun invoke(level: WordLevel, count: Int, offset: Int): Flow<List<Word>> =
        wordRepository.getDailyWords(level, count, offset)
}