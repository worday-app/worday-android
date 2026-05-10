package com.wordayapp.worday.data.local.database.dao

import androidx.room.*
import com.wordayapp.worday.data.local.database.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Update
    suspend fun updateWords(words: List<WordEntity>)

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): WordEntity?

    @Query("SELECT * FROM words WHERE nextReviewDate <= :now")
    fun getWordsDueForReview(now: Long): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE level = :level LIMIT :count")
    fun getDailyWords(level: String, count: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isSaved = 1")
    fun getSavedWords(): Flow<List<WordEntity>>

    @Query("UPDATE words SET isSaved = 1 WHERE id = :wordId")
    suspend fun saveWord(wordId: Int)

    @Query("UPDATE words SET isSaved = 0 WHERE id = :wordId")
    suspend fun unsaveWord(wordId: Int)

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 20")
    suspend fun getLevelTestWords(): List<WordEntity>

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    fun getLearnedWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getTotalWordCount(): Int

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<WordEntity>)
}