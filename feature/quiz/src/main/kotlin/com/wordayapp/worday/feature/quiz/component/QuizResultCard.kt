package com.wordayapp.worday.feature.quiz.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.ui.theme.WordayTheme

@Composable
fun QuizResultCard(
    totalWords: Int,
    correctCount: Int,
    wrongCount: Int,
    modifier: Modifier = Modifier
) {
    val accuracy = if (totalWords == 0) 0f
    else correctCount.toFloat() / totalWords.toFloat()

    val emoji = when {
        accuracy >= 0.9f -> "🏆"
        accuracy >= 0.7f -> "🎉"
        accuracy >= 0.5f -> "👍"
        else -> "💪"
    }

    val message = when {
        accuracy >= 0.9f -> "Outstanding!"
        accuracy >= 0.7f -> "Great job!"
        accuracy >= 0.5f -> "Good effort!"
        else -> "Keep practicing!"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = emoji, style = MaterialTheme.typography.displayMedium)

            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "${(accuracy * 100).toInt()}% accuracy",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultStat(
                    value = correctCount.toString(),
                    label = "Correct",
                    color = WordayTheme.colors.learned
                )
                ResultStat(
                    value = wrongCount.toString(),
                    label = "Wrong",
                    color = WordayTheme.colors.wrong
                )
                ResultStat(
                    value = totalWords.toString(),
                    label = "Total",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ResultStat(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}