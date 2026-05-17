package com.wordayapp.worday.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.domain.model.DailySession
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.repository.UserProgressRepository
import com.wordayapp.worday.domain.usecase.GetDailyWordsUseCase
import com.wordayapp.worday.domain.usecase.GetUserProgressUseCase
import com.wordayapp.worday.domain.usecase.RecordDailySessionUseCase
import com.wordayapp.worday.domain.usecase.UpdateWordAfterQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class QuizState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val isPassed: Boolean = false,       // ← YENİ
    val selectedChoiceIndex: Int? = null,
    val correctChoiceIndex: Int = 0,
    val choices: List<String> = emptyList(),
    val isAlreadyCompleted: Boolean = false
) {
    val currentWord: Word? get() = words.getOrNull(currentIndex)
    val totalWords: Int get() = words.size
    val progress: Float get() = if (totalWords == 0) 0f else currentIndex.toFloat() / totalWords.toFloat()
    val accuracyRate: Float get() = if (totalWords == 0) 0f else correctCount.toFloat() / totalWords.toFloat()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getDailyWords: GetDailyWordsUseCase,
    private val updateWordAfterQuiz: UpdateWordAfterQuizUseCase,
    private val recordDailySession: RecordDailySessionUseCase,
    private val getUserProgress: GetUserProgressUseCase,
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    init { loadWords() }

    private fun loadWords() {
        viewModelScope.launch {
            val progress = getUserProgress().first()

            // Bugün quiz zaten tamamlandıysa yeni kelime yükleme
            if (isSameDay(System.currentTimeMillis(), progress.lastQuizCompletedDate)) {
                _state.update { it.copy(isLoading = false, isAlreadyCompleted = true) }
                return@launch
            }

            val level = progress.level
            val goal = progress.dailyGoal
            val offset = progress.todayOffset  // gün boyunca sabit

            getDailyWords(level, goal, offset)
                .catch { _state.update { it.copy(isLoading = false) } }
                .first()
                .let { words ->
                    if (words.isNotEmpty()) {
                        val today = java.time.LocalDate.now()
                        val seed = (today.year * 10000 + today.monthValue * 100 + today.dayOfMonth).toLong()
                        val shuffled = words.shuffled(java.util.Random(seed))
                        val first = shuffled.first()
                        val wrong = shuffled.filter { it.id != first.id }
                            .shuffled(java.util.Random(seed + 1)).take(3).map { it.turkish }
                        val allChoices = (wrong + first.turkish).shuffled(java.util.Random(seed + 2))
                        _state.update {
                            it.copy(
                                words = shuffled,
                                isLoading = false,
                                choices = allChoices,
                                correctChoiceIndex = allChoices.indexOf(first.turkish),
                                selectedChoiceIndex = null
                            )
                        }
                    }
                }
        }
    }

    // Extension — deterministic shuffle with seed
    private fun <T> List<T>.shuffledBy(seed: Long): List<T> =
        this.shuffled(java.util.Random(seed))

    fun onChoiceSelected(choiceIndex: Int) {
        val s = _state.value
        if (s.selectedChoiceIndex != null || s.currentWord == null || s.isFinished) return

        val isCorrect = choiceIndex == s.correctChoiceIndex

        viewModelScope.launch {
            s.currentWord?.let { word -> updateWordAfterQuiz(word, isCorrect) }
        }

        _state.update {
            it.copy(
                selectedChoiceIndex = choiceIndex,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                wrongCount = if (!isCorrect) it.wrongCount + 1 else it.wrongCount
            )
        }

        viewModelScope.launch {
            delay(900)
            goNext()
        }
    }

    private fun goNext() {
        val s = _state.value
        val nextIndex = s.currentIndex + 1
        if (nextIndex >= s.totalWords) {
            finishQuiz()
        } else {
            val current = s.words.getOrNull(nextIndex) ?: return
            val wrong = s.words.filter { it.id != current.id }.shuffled().take(3).map { it.turkish }
            val allChoices = (wrong + current.turkish).shuffled()
            _state.update {
                it.copy(
                    currentIndex = nextIndex,
                    choices = allChoices,
                    correctChoiceIndex = allChoices.indexOf(current.turkish),
                    selectedChoiceIndex = null
                )
            }
        }
    }

    private fun finishQuiz() {

        if (_state.value.isFinished) return
        _state.update { it.copy(isFinished = true) }

        viewModelScope.launch {
            val s = _state.value
            val isPassed = s.correctCount.toFloat() / s.totalWords >= 0.6f

            recordDailySession(
                DailySession(
                    date = System.currentTimeMillis(),
                    wordsStudied = s.totalWords,
                    correctAnswers = s.correctCount,
                    totalAnswers = s.totalWords,
                    isCompleted = isPassed
                )
            )

            if (isPassed) {
                val progress = getUserProgress().first()
                val now = System.currentTimeMillis()

                android.util.Log.d("STREAK_DEBUG", "Quiz bitmeden önce streak: ${progress.currentStreak}")
                android.util.Log.d("STREAK_DEBUG", "lastQuizCompletedDate: ${progress.lastQuizCompletedDate}")

                val shouldIncrementStreak = !isSameDay(now, progress.lastQuizCompletedDate)

                android.util.Log.d("STREAK_DEBUG", "shouldIncrement: $shouldIncrementStreak")

                userProgressRepository.completeQuiz(progress.dailyGoal)

                if (shouldIncrementStreak) {
                    android.util.Log.d("STREAK_DEBUG", "incrementStreak çağrılıyor")
                    userProgressRepository.incrementStreak()
                }
            }

            _state.update { it.copy(isPassed = isPassed) }
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

