package com.wordayapp.worday.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DAILY_WORD_GOAL = intPreferencesKey("daily_word_goal")
        val IS_SOUND_ENABLED = booleanPreferencesKey("is_sound_enabled")
        val IS_NOTIFICATION_ENABLED = booleanPreferencesKey("is_notification_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val SELECTED_LEVEL = stringPreferencesKey("selected_level")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val FREE_AI_USAGE_COUNT = intPreferencesKey("free_ai_usage_count")
        val LAST_AI_USAGE_DATE = longPreferencesKey("last_ai_usage_date")
    }

    val dailyWordGoal: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.DAILY_WORD_GOAL] ?: 10 }

    val isSoundEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.IS_SOUND_ENABLED] ?: true }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.IS_NOTIFICATION_ENABLED] ?: true }

    val notificationHour: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.NOTIFICATION_HOUR] ?: 20 }

    val selectedLevel: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.SELECTED_LEVEL] ?: "A1" }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.IS_ONBOARDING_COMPLETED] ?: false }

    val freeAiUsageCount: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.FREE_AI_USAGE_COUNT] ?: 0 }

    val lastAiUsageDate: Flow<Long> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.LAST_AI_USAGE_DATE] ?: 0L }

    suspend fun setDailyWordGoal(goal: Int) {
        context.dataStore.edit { it[Keys.DAILY_WORD_GOAL] = goal }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_SOUND_ENABLED] = enabled }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setNotificationHour(hour: Int) {
        context.dataStore.edit { it[Keys.NOTIFICATION_HOUR] = hour }
    }

    suspend fun setSelectedLevel(level: String) {
        context.dataStore.edit { it[Keys.SELECTED_LEVEL] = level }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { it[Keys.IS_ONBOARDING_COMPLETED] = true }
    }

    suspend fun incrementAiUsageCount() {
        context.dataStore.edit {
            it[Keys.FREE_AI_USAGE_COUNT] = (it[Keys.FREE_AI_USAGE_COUNT] ?: 0) + 1
        }
    }

    suspend fun resetAiUsageCount() {
        context.dataStore.edit {
            it[Keys.FREE_AI_USAGE_COUNT] = 0
            it[Keys.LAST_AI_USAGE_DATE] = System.currentTimeMillis()
        }
    }
}