package com.wordayapp.worday.feature.stats.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.feature.stats.component.*
import com.wordayapp.worday.feature.stats.viewmodel.StatsViewModel
import com.wordayapp.worday.ui.component.LoadingIndicator

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.progressState is UiState.Loading) {
        LoadingIndicator()
        return
    }

    val progress = (state.progressState as? UiState.Success)?.data ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your stats",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        StreakCard(
            currentStreak = progress.currentStreak,
            longestStreak = progress.longestStreak
        )

        StatGrid(
            progress = progress,
            dueCount = state.sessions.count { !it.isCompleted }
        )

        WeeklyChart(
            data = state.weeklyData,
            maxValue = state.weeklyMax
        )

        LevelDistributionCard(progress = progress)

        Spacer(modifier = Modifier.height(80.dp))
    }
}