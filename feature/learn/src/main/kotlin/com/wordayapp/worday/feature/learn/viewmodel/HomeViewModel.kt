package com.wordayapp.worday.feature.learn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.usecase.GetUserProgressUseCase
import com.wordayapp.worday.domain.usecase.GetWordsDueForReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val progressState: UiState<UserProgress> = UiState.Idle,
    val reviewWords: List<Word> = emptyList(),
    val dailyGoal: Int = 10,
    val todayStudied: Int = 0,
    val userName: String = "there"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProgress: GetUserProgressUseCase,
    private val getWordsDueForReview: GetWordsDueForReviewUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Günlük hedef
            dataStore.getDailyWordGoal().collect { goal ->
                _state.update { it.copy(dailyGoal = goal) }
            }
        }

        viewModelScope.launch {
            getUserProgress()
                .catch {
                    _state.update {
                        it.copy(progressState = UiState.Error("Yüklenemedi"))
                    }
                }
                .collect { progress ->
                    _state.update {
                        it.copy(
                            progressState = UiState.Success(progress),
                            todayStudied = progress.totalWordsLearned // günlük session ile güncellenir
                        )
                    }
                }
        }

        viewModelScope.launch {
            getWordsDueForReview()
                .catch { /* sessizce geç */ }
                .collect { words ->
                    _state.update { it.copy(reviewWords = words.take(5)) }
                }
        }
    }
}