package com.wordayapp.worday.feature.learn.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.common.ui.UiEvent
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.feature.learn.component.WordCard
import com.wordayapp.worday.feature.learn.viewmodel.LearnViewModel
import com.wordayapp.worday.ui.component.LoadingIndicator
import com.wordayapp.worday.ui.component.WordayButton
import com.wordayapp.worday.ui.component.WordayOutlinedButton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LearnScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // UiEvent dinle
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Navigate -> if (event.route == "quiz") onNavigateToQuiz()
                is UiEvent.NavigateBack -> onNavigateBack()
                else -> Unit
            }
        }
    }

    // Tüm kelimeler bitti
    if (state.isFinished) {
        FinishedContent(onNavigateBack = onNavigateBack)
        return
    }

    when (val wordsState = state.wordsState) {
        is UiState.Loading -> LoadingIndicator()

        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = wordsState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is UiState.Success -> {
            val word = state.currentWord
            if (word == null) {
                LoadingIndicator()
                return
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            "← Back",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "${state.currentIndex + 1} / ${state.totalWords}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline
                )

                // Kelime kartı
                WordCard(
                    word = word,
                    onTtsClick = { viewModel.onSpeakWord() },
                    modifier = Modifier.weight(1f)
                )

                // Butonlar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WordayOutlinedButton(
                        text = "Don't know",
                        onClick = { viewModel.onDontKnow() },
                        modifier = Modifier.weight(1f)
                    )
                    WordayButton(
                        text = "I know this",
                        onClick = { viewModel.onKnow() },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        else -> Unit
    }
}

@Composable
private fun FinishedContent(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎉", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Session complete!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Great job! Come back tomorrow to keep your streak.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        WordayButton(
            text = "Back to Home",
            onClick = onNavigateBack
        )
    }
}