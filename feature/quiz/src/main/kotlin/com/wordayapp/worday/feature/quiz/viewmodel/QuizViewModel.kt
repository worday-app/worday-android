package com.wordayapp.worday.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.domain.algorithm.Sm2Algorithm
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.usecase.GetDailyWordsUseCase
import com.wordayapp.worday.domain.usecase.RecordDailySessionUseCase
import com.wordayapp.worday.domain.usecase.UpdateWordAfterQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val selectedChoiceIndex: Int? = null,
    val correctChoiceIndex: Int = 0,
    val choices: List<String> = emptyList()
) {
    val currentWord: Word? get() = words.getOrNull(currentIndex)
    val totalWords: Int get() = words.size
    val progress: Float get() = if (totalWords == 0) 0f
    else currentIndex.toFloat() / totalWords.toFloat()
    val accuracyRate: Float get() = if (totalWords == 0) 0f
    else correctCount.toFloat() / totalWords.toFloat()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getDailyWords: GetDailyWordsUseCase,
    private val updateWordAfterQuiz: UpdateWordAfterQuizUseCase,
    private val recordDailySession: RecordDailySessionUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            val levelName = dataStore.getSelectedLevel().first()
            val goal = dataStore.getDailyWordGoal().first()
            val level = runCatching { WordLevel.valueOf(levelName) }.getOrDefault(WordLevel.A1)

            getDailyWords(level, goal)
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { words ->
                    if (words.isNotEmpty()) {
                        val shuffled = words.shuffled()
                        _state.update {
                            it.copy(
                                words = shuffled,
                                isLoading = false
                            )
                        }
                        prepareChoices(shuffled, 0)
                    }
                }
        }
    }

    /**
     * Mevcut kelime için 4 seçenek hazırlar.
     * 1 doğru + 3 yanlış (diğer kelimelerden rastgele).
     */
    private fun prepareChoices(words: List<Word>, index: Int) {
        val current = words.getOrNull(index) ?: return
        val wrong = words
            .filter { it.id != current.id }
            .shuffled()
            .take(3)
            .map { it.turkish }

        val allChoices = (wrong + current.turkish).shuffled()
        val correctIndex = allChoices.indexOf(current.turkish)

        _state.update {
            it.copy(
                choices = allChoices,
                correctChoiceIndex = correctIndex,
                selectedChoiceIndex = null
            )
        }
    }

    fun onChoiceSelected(choiceIndex: Int) {
        val s = _state.value
        if (s.selectedChoiceIndex != null) return  // zaten cevaplandı
        if (s.currentWord == null) return

        val isCorrect = choiceIndex == s.correctChoiceIndex

        // SM-2 quality: doğru = 4, yanlış = 1
        val quality = if (isCorrect) 4 else 1

        viewModelScope.launch {
            s.currentWord?.let { word ->
                updateWordAfterQuiz(word, quality)
            }
        }

        _state.update {
            it.copy(
                selectedChoiceIndex = choiceIndex,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount,
                wrongCount = if (!isCorrect) it.wrongCount + 1 else it.wrongCount
            )
        }

        // 900ms sonra sonraki soruya geç
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
            _state.update { it.copy(currentIndex = nextIndex) }
            prepareChoices(s.words, nextIndex)
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            val s = _state.value
            recordDailySession(
                wordsStudied = s.totalWords,
                correctAnswers = s.correctCount,
                totalAnswers = s.totalWords
            )
        }
        _state.update { it.copy(isFinished = true) }
    }
}