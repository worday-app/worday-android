package com.wordayapp.worday.feature.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.domain.model.WordLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val selectedGoal: Int = 10,
    val selectedLevel: WordLevel = WordLevel.A1,
    val isCompleting: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun setGoal(goal: Int) {
        _state.value = _state.value.copy(selectedGoal = goal)
    }

    fun setLevel(level: WordLevel) {
        _state.value = _state.value.copy(selectedLevel = level)
    }

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCompleting = true)
            val s = _state.value
            dataStore.setDailyWordGoal(s.selectedGoal)
            dataStore.setSelectedLevel(s.selectedLevel.name)
            dataStore.setOnboardingCompleted(true)
            onDone()
        }
    }
}