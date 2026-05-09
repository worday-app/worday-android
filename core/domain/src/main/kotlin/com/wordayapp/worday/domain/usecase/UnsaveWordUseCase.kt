package com.wordayapp.worday.domain.usecase

import javax.inject.Inject

class UnsaveWordUseCase @Inject constructor(
    private val saveWordUseCase: SaveWordUseCase
) {
    suspend operator fun invoke(wordId: Int) {
        saveWordUseCase(wordId, false)
    }
}