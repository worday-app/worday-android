package com.wordayapp.worday.feature.onboarding.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.feature.onboarding.viewmodel.LevelTestViewModel
import com.wordayapp.worday.ui.component.LoadingIndicator
import com.wordayapp.worday.ui.theme.WordayTheme
import kotlinx.coroutines.delay

@Composable
fun LevelTestScreen(
    onFinished: (WordLevel) -> Unit,
    onSkip: () -> Unit,
    viewModel: LevelTestViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    val word = state.currentWord ?: return

    // Cevap seçildikten 800ms sonra sonraki soruya geç
    LaunchedEffect(state.selectedAnswerIndex) {
        if (state.selectedAnswerIndex != null) {
            delay(800)
            viewModel.nextQuestion(onFinished)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Question ${state.currentIndex + 1} of ${state.words.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline
            )
        }

        // Word card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "What does this word mean?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = word.english,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = word.ipa,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            SuggestionChip(
                onClick = {},
                label = { Text(word.level.name, style = MaterialTheme.typography.labelSmall) }
            )
        }

        // Choices
        // Gerçek uygulamada 3 yanlış + 1 doğru kelime DB'den gelir.
        // Şimdilik Turkish karşılığı + 3 placeholder.
        val choices = remember(word) {
            listOf(
                word.turkish,          // index 0 = doğru
                "placeholder 1",
                "placeholder 2",
                "placeholder 3"
            ).shuffled()
        }
        val correctIndex = remember(choices) { choices.indexOf(word.turkish) }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            choices.forEachIndexed { index, choice ->
                ChoiceButton(
                    text = choice,
                    state = when {
                        state.selectedAnswerIndex == null -> ChoiceState.Idle
                        index == correctIndex -> ChoiceState.Correct
                        index == state.selectedAnswerIndex -> ChoiceState.Wrong
                        else -> ChoiceState.Idle
                    },
                    enabled = state.selectedAnswerIndex == null,
                    onClick = {
                        viewModel.onAnswerSelected(index, index == correctIndex)
                    }
                )
            }
        }

        TextButton(
            onClick = onSkip,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Skip level test",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

enum class ChoiceState { Idle, Correct, Wrong }

@Composable
private fun ChoiceButton(
    text: String,
    state: ChoiceState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when (state) {
        ChoiceState.Correct -> WordayTheme.colors.learned.copy(alpha = 0.15f)
        ChoiceState.Wrong -> WordayTheme.colors.wrong.copy(alpha = 0.12f)
        ChoiceState.Idle -> MaterialTheme.colorScheme.background
    }
    val borderColor = when (state) {
        ChoiceState.Correct -> WordayTheme.colors.learned
        ChoiceState.Wrong -> WordayTheme.colors.wrong
        ChoiceState.Idle -> MaterialTheme.colorScheme.outline
    }
    val textColor = when (state) {
        ChoiceState.Correct -> WordayTheme.colors.learned
        ChoiceState.Wrong -> WordayTheme.colors.wrong
        ChoiceState.Idle -> MaterialTheme.colorScheme.onBackground
    }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = textColor,
            disabledContainerColor = containerColor,
            disabledContentColor = textColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (state != ChoiceState.Idle) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}