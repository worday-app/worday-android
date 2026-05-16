package com.wordayapp.worday.feature.learn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.common.ui.UiEvent
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.domain.usecase.GetDailyWordsUseCase
import com.wordayapp.worday.domain.usecase.SaveWordUseCase
import com.wordayapp.worday.ui.tts.TtsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnState(
    val wordsState: UiState<List<Word>> = UiState.Loading,
    val currentIndex: Int = 0,
    val isFinished: Boolean = false
) {
    val currentWord: Word?
        get() = (wordsState as? UiState.Success)?.data?.getOrNull(currentIndex)

    val totalWords: Int
        get() = (wordsState as? UiState.Success)?.data?.size ?: 0

    val progress: Float
        get() = if (totalWords == 0) 0f
        else currentIndex.toFloat() / totalWords.toFloat()
}

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getDailyWords: GetDailyWordsUseCase,
    private val saveWord: SaveWordUseCase,
    private val dataStore: UserPreferencesDataStore,
    private val ttsHelper: TtsHelper
) : ViewModel() {

    private val _state = MutableStateFlow(LearnState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            val levelName = dataStore.selectedLevel.first()
            val goal = dataStore.dailyWordGoal.first()
            val level = runCatching { WordLevel.valueOf(levelName) }.getOrDefault(WordLevel.A1)

            val today = java.time.LocalDate.now()
            val seed = (today.year * 10000 + today.monthValue * 100 + today.dayOfMonth).toLong()

            getDailyWords(level, goal, seed)
                .catch { _state.update { it.copy(wordsState = UiState.Error("Kelimeler yüklenemedi")) } }
                .collect { words ->
                    _state.update { it.copy(wordsState = UiState.Success(words)) }
                }
        }
    }

    fun onKnow() {
        val total = _state.value.totalWords
        val next = _state.value.currentIndex + 1
        if (next >= total) {
            _state.update { it.copy(isFinished = true) }
        } else {
            _state.update { it.copy(currentIndex = next) }
        }
    }

    fun onDontKnow() {
        // Quiz'e yönlendir — quiz bu kelimeyi tekrar sorar
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("quiz"))
        }
    }

    fun onSaveWord() {
        val word = _state.value.currentWord ?: return
        viewModelScope.launch {
            saveWord(word.id, true)
        }
    }

    fun onSpeakWord() {
        val word = _state.value.currentWord ?: return
        ttsHelper.speak(word.english)
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.stop()
    }
}