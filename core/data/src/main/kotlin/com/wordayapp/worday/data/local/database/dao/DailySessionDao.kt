package com.wordayapp.worday.data.local.database.dao

import androidx.room.*
import com.wordayapp.worday.data.local.database.entity.DailySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DailySessionEntity)

    @Update
    suspend fun updateSession(session: DailySessionEntity)

    @Query("SELECT * FROM daily_sessions WHERE date = :dayStartMillis LIMIT 1")
    suspend fun getSessionByDate(dayStartMillis: Long): DailySessionEntity?

    @Query("SELECT * FROM daily_sessions ORDER BY date DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<DailySessionEntity>>

    @Query("SELECT * FROM daily_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<DailySessionEntity>>
}