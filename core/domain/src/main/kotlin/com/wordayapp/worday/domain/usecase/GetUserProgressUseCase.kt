package com.wordayapp.worday.domain.usecase

import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
) {
    operator fun invoke(): Flow<UserProgress> =
        userProgressRepository.getUserProgress()
}