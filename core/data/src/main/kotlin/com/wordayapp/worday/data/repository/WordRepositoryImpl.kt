package com.wordayapp.worday.data.repository

import com.wordayapp.worday.data.local.database.dao.WordDao
import com.wordayapp.worday.data.local.database.entity.WordEntity
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.model.WordType
import com.wordayapp.worday.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {

    override fun getWordsDueForReview(): Flow<List<Word>> =
        wordDao.getWordsDueForReview(System.currentTimeMillis()).map { list ->
            list.map { it.toDomain() }
        }

    override fun getDailyWords(level: WordLevel, count: Int, seed: Long): Flow<List<Word>> =
        wordDao.getDailyWords(level.name, count, seed).map { list ->
            list.map { it.toDomain() }
        }

    override fun getSavedWords(): Flow<List<Word>> =
        wordDao.getSavedWords().map { list -> list.map { it.toDomain() } }

    override suspend fun getWordById(id: Int): Word? =
        wordDao.getWordById(id)?.toDomain()

    override suspend fun updateWord(word: Word) =
        wordDao.updateWord(word.toEntity())

    override suspend fun updateWords(words: List<Word>) =
        wordDao.updateWords(words.map { it.toEntity() })

    override suspend fun saveWord(wordId: Int) =
        wordDao.saveWord(wordId)

    override suspend fun unsaveWord(wordId: Int) =
        wordDao.unsaveWord(wordId)

    override suspend fun getLevelTestWords(): List<Word> =
        wordDao.getLevelTestWords().map { it.toDomain() }

    // --- Mapper'lar ---

    private fun WordEntity.toDomain() = Word(
        id = id,
        english = english,
        turkish = turkish,
        definition = definition,
        ipa = ipa,
        exampleSentence1 = exampleSentence1,
        exampleSentence2 = exampleSentence2,
        level = WordLevel.valueOf(level),
        type = WordType.valueOf(type),
        easeFactor = easeFactor,
        intervalDays = intervalDays,
        repetitionCount = repetitionCount,
        isLearned = isLearned,
        isSaved = isSaved,
        nextReviewDate = nextReviewDate
    )

    private fun Word.toEntity() = WordEntity(
        id = id,
        english = english,
        turkish = turkish,
        definition = definition,
        ipa = ipa,
        exampleSentence1 = exampleSentence1,
        exampleSentence2 = exampleSentence2,
        level = level.name,
        type = type.name,
        easeFactor = easeFactor,
        intervalDays = intervalDays,
        repetitionCount = repetitionCount,
        isLearned = isLearned,
        isSaved = isSaved,
        nextReviewDate = nextReviewDate
    )
}