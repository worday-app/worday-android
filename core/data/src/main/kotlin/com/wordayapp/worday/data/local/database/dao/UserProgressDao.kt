package com.wordayapp.worday.data.local.database.dao

import androidx.room.*
import com.wordayapp.worday.data.local.database.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(progress: UserProgressEntity)

    @Update
    suspend fun update(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = 1")
    fun observeProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE userId = 1")
    suspend fun getProgress(): UserProgressEntity?

    @Query("UPDATE user_progress SET currentStreak = 0 WHERE userId = 1")
    suspend fun resetStreak()

    @Query("UPDATE user_progress SET currentStreak = currentStreak + 1, longestStreak = MAX(longestStreak, currentStreak + 1) WHERE userId = 1")
    suspend fun incrementStreak()

    @Query("UPDATE user_progress SET lastStudyDate = :date WHERE userId = 1")
    suspend fun updateLastStudyDate(date: Long)

    @Query("UPDATE user_progress SET todayOffset = wordOffset, todaySeenCount = 0, lastDayStartDate = :date WHERE userId = 1")
    suspend fun startNewDay(date: Long)

    @Query("UPDATE user_progress SET todaySeenCount = :count WHERE userId = 1")
    suspend fun updateTodaySeenCount(count: Int)

    @Query("UPDATE user_progress SET wordOffset = wordOffset + :goal, lastQuizCompletedDate = :date, lastStudyDate = :date WHERE userId = 1")
    suspend fun completeQuiz(goal: Int, date: Long)
}