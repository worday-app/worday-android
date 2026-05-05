package com.wordayapp.worday.feature.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.usecase.GetLevelTestWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LevelTestState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val detectedLevel: WordLevel = WordLevel.A1,
    val selectedAnswerIndex: Int? = null,   // kullanıcının seçimi
    val correctAnswerIndex: Int? = null     // doğru cevabın indexi
) {
    val currentWord: Word? get() = words.getOrNull(currentIndex)
    val progress: Float get() = if (words.isEmpty()) 0f
    else currentIndex.toFloat() / words.size.toFloat()
}

@HiltViewModel
class LevelTestViewModel @Inject constructor(
    private val getLevelTestWords: GetLevelTestWordsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LevelTestState())
    val state = _state.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            val words = getLevelTestWords()
            _state.value = _state.value.copy(
                words = words,
                isLoading = false
            )
        }
    }

    /**
     * Kullanıcı bir seçenek seçti.
     * [choiceIndex] — seçilen şık indexi (0-3)
     * [isCorrect] — doğru mu?
     */
    fun onAnswerSelected(choiceIndex: Int, isCorrect: Boolean) {
        val s = _state.value
        if (s.selectedAnswerIndex != null) return  // zaten cevaplandı

        val correct = if (isCorrect) s.correctCount + 1 else s.correctCount
        // Doğru şıkkın indexini bul (UI'dan gelecek, basit mock için 0 kabul et)
        _state.value = s.copy(
            selectedAnswerIndex = choiceIndex,
            correctCount = correct
        )
    }

    /** Kısa gecikme sonrası çağrılır, sonraki soruya geçer. */
    fun nextQuestion(onFinished: (WordLevel) -> Unit) {
        val s = _state.value
        val nextIndex = s.currentIndex + 1

        if (nextIndex >= s.words.size) {
            val level = calculateLevel(s.correctCount, s.words.size)
            _state.value = s.copy(isFinished = true, detectedLevel = level)
            onFinished(level)
        } else {
            _state.value = s.copy(
                currentIndex = nextIndex,
                selectedAnswerIndex = null,
                correctAnswerIndex = null
            )
        }
    }

    private fun calculateLevel(correct: Int, total: Int): WordLevel {
        val ratio = correct.toFloat() / total.toFloat()
        return when {
            ratio < 0.20f -> WordLevel.A1
            ratio < 0.35f -> WordLevel.A2
            ratio < 0.50f -> WordLevel.B1
            ratio < 0.65f -> WordLevel.B2
            ratio < 0.80f -> WordLevel.C1
            else -> WordLevel.C2
        }
    }
}