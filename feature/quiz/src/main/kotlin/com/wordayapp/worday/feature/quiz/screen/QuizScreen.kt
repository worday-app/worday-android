package com.wordayapp.worday.feature.quiz.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.feature.quiz.component.*
import com.wordayapp.worday.feature.quiz.viewmodel.QuizViewModel
import com.wordayapp.worday.ui.component.LoadingIndicator

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateToResult: (Int, Int, Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onNavigateToResult(state.correctCount, state.totalWords, state.isPassed)
        }
    }

    // Bugün zaten tamamlandı
    if (state.isAlreadyCompleted) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🎉", fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp))
                Text(
                    text = "Bugünü zaten tamamladın!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Yarın yeni kelimeler seni bekliyor.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    val word = state.currentWord ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quiz · ${state.currentIndex + 1} of ${state.totalWords}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "✓ ${state.correctCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = com.wordayapp.worday.ui.theme.WordayTheme.colors.learned
                )
                Text(
                    text = "✗ ${state.wrongCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = com.wordayapp.worday.ui.theme.WordayTheme.colors.wrong
                )
            }
        }

        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline
        )

        QuizWordCard(word = word)

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            state.choices.forEachIndexed { index, choice ->
                val choiceState = when {
                    state.selectedChoiceIndex == null -> ChoiceState.Idle
                    index == state.correctChoiceIndex -> ChoiceState.Correct
                    index == state.selectedChoiceIndex -> ChoiceState.Wrong
                    else -> ChoiceState.Idle
                }
                QuizChoiceButton(
                    text = choice,
                    state = choiceState,
                    enabled = state.selectedChoiceIndex == null,
                    onClick = { viewModel.onChoiceSelected(index) }
                )
            }
        }
    }
}