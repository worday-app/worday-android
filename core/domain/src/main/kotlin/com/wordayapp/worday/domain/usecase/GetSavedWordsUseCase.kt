package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.repository.WordRepository
import javax.inject.Inject

class GetSavedWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    operator fun invoke() =
        wordRepository.getSavedWords()
}