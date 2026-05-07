package com.wordayapp.worday.feature.wordbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.domain.usecase.GetSavedWordsUseCase
import com.wordayapp.worday.domain.usecase.SaveWordUseCase
import com.wordayapp.worday.domain.usecase.UnsaveWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WordbookFilter { All, Learning, Learned }

data class WordbookState(
    val allWords: List<Word> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val activeFilter: WordbookFilter = WordbookFilter.All
) {
    val filteredWords: List<Word>
        get() {
            val byFilter = when (activeFilter) {
                WordbookFilter.All -> allWords
                WordbookFilter.Learning -> allWords.filter { !it.isLearned }
                WordbookFilter.Learned -> allWords.filter { it.isLearned }
            }
            return if (searchQuery.isBlank()) byFilter
            else byFilter.filter {
                it.english.contains(searchQuery, ignoreCase = true) ||
                        it.turkish.contains(searchQuery, ignoreCase = true)
            }
        }

    val allCount: Int get() = allWords.size
    val learningCount: Int get() = allWords.count { !it.isLearned }
    val learnedCount: Int get() = allWords.count { it.isLearned }
}

@HiltViewModel
class WordbookViewModel @Inject constructor(
    private val getSavedWords: GetSavedWordsUseCase,
    private val unsaveWord: UnsaveWordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WordbookState())
    val state = _state.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            getSavedWords()
                .catch { _state.update { it.copy(isLoading = false) } }
                .collect { words ->
                    _state.update {
                        it.copy(allWords = words, isLoading = false)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onFilterChange(filter: WordbookFilter) {
        _state.update { it.copy(activeFilter = filter) }
    }

    fun onUnsaveWord(wordId: Int) {
        viewModelScope.launch {
            unsaveWord(wordId)
        }
    }
}