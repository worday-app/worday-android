package com.wordayapp.worday.domain.repository

import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    fun getUserProgress(): Flow<UserProgress>
    suspend fun updateProgress(progress: UserProgress)
    suspend fun recordDailySession(session: DailySession)
    fun getDailySessions(limit: Int = 30): Flow<List<DailySession>>
    suspend fun resetDailyStreak()
    suspend fun incrementStreak()
}
