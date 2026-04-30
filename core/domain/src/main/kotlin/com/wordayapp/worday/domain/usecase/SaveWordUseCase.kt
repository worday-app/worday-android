package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.repository.WordRepository
import javax.inject.Inject

class SaveWordUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(wordId: Int, save: Boolean) {
        if (save) wordRepository.saveWord(wordId)
        else wordRepository.unsaveWord(wordId)
    }
}