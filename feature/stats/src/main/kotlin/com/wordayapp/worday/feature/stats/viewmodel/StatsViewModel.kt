package com.wordayapp.worday.feature.stats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.usecase.GetDailySessionsUseCase
import com.wordayapp.worday.domain.usecase.GetUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsState(
    val progressState: UiState<UserProgress> = UiState.Loading,
    val sessions: List<DailySession> = emptyList()
) {
    // Son 7 günün session'larını weekday sırasıyla döner
    val weeklyData: List<Int>
        get() {
            val last7 = sessions.takeLast(7)
            return if (last7.size < 7) {
                List(7 - last7.size) { 0 } + last7.map { it.wordsStudied }
            } else {
                last7.map { it.wordsStudied }
            }
        }

    val weeklyMax: Int get() = weeklyData.maxOrNull()?.coerceAtLeast(1) ?: 1
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getUserProgress: GetUserProgressUseCase,
    private val getDailySessions: GetDailySessionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            getUserProgress()
                .catch {
                    _state.update {
                        it.copy(progressState = UiState.Error("Yüklenemedi"))
                    }
                }
                .collect { progress ->
                    _state.update {
                        it.copy(progressState = UiState.Success(progress))
                    }
                }
        }

        viewModelScope.launch {
            getDailySessions(limit = 30)
                .catch { }
                .collect { sessions ->
                    _state.update { it.copy(sessions = sessions) }
                }
        }
    }
}