package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.algorithm.Sm2Algorithm
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.repository.WordRepository
import javax.inject.Inject

class UpdateWordAfterQuizUseCase @Inject constructor(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(word: Word, isCorrect: Boolean): Word {
        val updatedWord = Sm2Algorithm.calculate(word, isCorrect)
        wordRepository.updateWord(updatedWord)
        return updatedWord
    }

    suspend operator fun invoke(words: List<Pair<Word, Boolean>>) {
        val updatedWords = words.map { (word, isCorrect) ->
            Sm2Algorithm.calculate(word, isCorrect)
        }
        wordRepository.updateWords(updatedWords)
    }
}