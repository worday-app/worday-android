package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailySessionsUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
) {
    operator fun invoke(limit: Int = 30): Flow<List<DailySession>> =
        userProgressRepository.getDailySessions(limit)
}