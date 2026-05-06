package com.wordayapp.worday.feature.learn.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.feature.learn.component.*
import com.wordayapp.worday.feature.learn.viewmodel.HomeViewModel
import com.wordayapp.worday.ui.component.WordayButton

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning, ${state.userName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ready to learn?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }

            val progress = state.progressState
            if (progress is UiState.Success) {
                StreakBadge(streak = progress.data.currentStreak)
            }
        }

        // Günlük progress
        DailyProgressCard(
            studied = state.todayStudied,
            goal = state.dailyGoal
        )

        // CTA
        WordayButton(
            text = "Continue learning →",
            onClick = onNavigateToLearn
        )

        // Review listesi
        if (state.reviewWords.isNotEmpty()) {
            Text(
                text = "Due for review",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.reviewWords.forEach { word ->
                    WordRowItem(word = word)
                }
            }
        }

        // Stat grid
        val progress = state.progressState
        if (progress is UiState.Success) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    value = progress.data.totalWordsLearned.toString(),
                    label = "words learned",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${(progress.data.accuracyRate * 100).toInt()}%",
                    label = "accuracy",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // bottom nav boşluğu
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}