package com.wordayapp.worday.data.repository

import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import com.wordayapp.worday.data.local.database.entity.DailySessionEntity
import com.wordayapp.worday.data.local.database.entity.UserProgressEntity
import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class UserProgressRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val dailySessionDao: DailySessionDao
) : UserProgressRepository {

    override fun getUserProgress(): Flow<UserProgress> =
        userProgressDao.observeProgress()
            .filterNotNull()
            .map { it.toDomain() }

    override suspend fun updateProgress(progress: UserProgress) {
        val existing = userProgressDao.getProgress()
        if (existing == null) {
            userProgressDao.insertOrReplace(progress.toEntity())
        } else {
            userProgressDao.update(progress.toEntity())
        }
    }

    override suspend fun recordDailySession(session: DailySession) =
        dailySessionDao.insertSession(session.toEntity())

    override fun getDailySessions(limit: Int): Flow<List<DailySession>> =
        dailySessionDao.getRecentSessions(limit).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun resetDailyStreak() =
        userProgressDao.resetStreak()

    override suspend fun incrementStreak() =
        userProgressDao.incrementStreak()

    // --- Mapper'lar ---

    private fun UserProgressEntity.toDomain() = UserProgress(
        userId = userId,
        level = WordLevel.valueOf(level),
        dailyGoal = dailyGoal,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastStudyDate = lastStudyDate,
        totalWordsLearned = totalWordsLearned,
        totalCorrectAnswers = totalCorrectAnswers,
        totalAnswers = totalAnswers
    )

    private fun UserProgress.toEntity() = UserProgressEntity(
        userId = userId,
        level = level.name,
        dailyGoal = dailyGoal,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastStudyDate = lastStudyDate,
        totalWordsLearned = totalWordsLearned,
        totalCorrectAnswers = totalCorrectAnswers,
        totalAnswers = totalAnswers
    )

    private fun DailySessionEntity.toDomain() = DailySession(
        id = id,
        date = date,
        wordsStudied = wordsStudied,
        correctAnswers = correctAnswers,
        totalAnswers = totalAnswers,
        isCompleted = isCompleted
    )

    private fun DailySession.toEntity() = DailySessionEntity(
        id = id,
        date = date,
        wordsStudied = wordsStudied,
        correctAnswers = correctAnswers,
        totalAnswers = totalAnswers,
        isCompleted = isCompleted
    )
}