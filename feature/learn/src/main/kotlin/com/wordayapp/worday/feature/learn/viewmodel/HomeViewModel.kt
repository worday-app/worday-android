package com.wordayapp.worday.feature.learn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.data.local.database.seed.SeedState
import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.repository.UserProgressRepository
import com.wordayapp.worday.domain.usecase.GetDailyWordsUseCase
import com.wordayapp.worday.domain.usecase.GetUserProgressUseCase
import com.wordayapp.worday.domain.usecase.GetWordsDueForReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val progressState: UiState<UserProgress> = UiState.Loading,
    val todayWords: List<Word> = emptyList(),
    val tomorrowWords: List<Word> = emptyList(),
    val reviewWords: List<Word> = emptyList(),
    val currentCardIndex: Int = 0,
    val isWordsLoading: Boolean = true,
    val dailyGoal: Int = 10,
    val todayQuizCompleted: Boolean = false,
) {
    val allWordsSeen: Boolean
        get() = todayWords.isNotEmpty() && currentCardIndex >= todayWords.size

    val todayStudied: Int
        get() = currentCardIndex.coerceAtMost(todayWords.size)

    val currentWord: Word?
        get() = todayWords.getOrNull(currentCardIndex)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProgress: GetUserProgressUseCase,
    private val getDailyWords: GetDailyWordsUseCase,
    private val getWordsDueForReview: GetWordsDueForReviewUseCase,
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeProgress()
        loadWords()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            getUserProgress()
                .catch { _state.update { it.copy(progressState = UiState.Error("Yüklenemedi")) } }
                .collect { progress ->
                    val now = System.currentTimeMillis()
                    _state.update {
                        it.copy(
                            progressState = UiState.Success(progress),
                            todayQuizCompleted = isSameDay(now, progress.lastQuizCompletedDate)
                        )
                    }
                }
        }
        viewModelScope.launch {
            getWordsDueForReview()
                .catch { }
                .collect { words -> _state.update { it.copy(reviewWords = words.take(5)) } }
        }
    }

    private fun loadWords() {
        viewModelScope.launch {
            // Seed bitene kadar bekle — zaten bittiyse anında geçer
            SeedState.isReady.filter { it }.first()

            var progress = getUserProgress().first()
            val now = System.currentTimeMillis()

            if (!isSameDay(now, progress.lastDayStartDate)) {
                userProgressRepository.startNewDay()
                progress = getUserProgress().first()
            }

            val level = progress.level
            val goal = progress.dailyGoal
            val todayOffset = progress.todayOffset
            val tomorrowOffset = todayOffset + goal

            val todayWords = getDailyWords(level, goal, todayOffset).first()
            val tomorrowWords = getDailyWords(level, goal, tomorrowOffset).first()

            _state.update {
                it.copy(
                    todayWords = todayWords,
                    tomorrowWords = tomorrowWords,
                    isWordsLoading = false,
                    dailyGoal = goal,
                    currentCardIndex = progress.todaySeenCount
                )
            }
        }
    }

    fun onCardSwiped() {
        val newIndex = _state.value.currentCardIndex + 1
        _state.update { it.copy(currentCardIndex = newIndex) }
        viewModelScope.launch {
            userProgressRepository.updateTodaySeenCount(newIndex)
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        if (t2 == 0L) return false
        val c1 = java.util.Calendar.getInstance().apply { timeInMillis = t1 }
        val c2 = java.util.Calendar.getInstance().apply { timeInMillis = t2 }
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR) &&
                c1.get(java.util.Calendar.DAY_OF_YEAR) == c2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}