package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.repository.UserProgressRepository
import javax.inject.Inject

class RecordDailySessionUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
) {
    suspend operator fun invoke(session: DailySession) {
        userProgressRepository.recordDailySession(session)
        if (session.isCompleted) {
            userProgressRepository.incrementStreak()
        }
    }
}