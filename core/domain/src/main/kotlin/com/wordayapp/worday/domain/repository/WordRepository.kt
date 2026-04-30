package com.wordayapp.worday.domain.repository

import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getWordsDueForReview(): Flow<List<Word>>
    fun getDailyWords(level: WordLevel, count: Int): Flow<List<Word>>
    fun getSavedWords(): Flow<List<Word>>
    suspend fun getWordById(id: Int): Word?
    suspend fun updateWord(word: Word)
    suspend fun updateWords(words: List<Word>)
    suspend fun saveWord(wordId: Int)
    suspend fun unsaveWord(wordId: Int)
    suspend fun getLevelTestWords(): List<Word>
}
