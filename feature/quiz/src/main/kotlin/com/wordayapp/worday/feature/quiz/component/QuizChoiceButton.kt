package com.wordayapp.worday.feature.quiz.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.ui.theme.WordayTheme

enum class ChoiceState { Idle, Correct, Wrong }

@Composable
fun QuizChoiceButton(
    text: String,
    state: ChoiceState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (state) {
        ChoiceState.Correct -> WordayTheme.colors.learned.copy(alpha = 0.12f)
        ChoiceState.Wrong -> WordayTheme.colors.wrong.copy(alpha = 0.10f)
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
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = textColor,
            disabledContainerColor = containerColor,
            disabledContentColor = textColor
        ),
        border = BorderStroke(
            width = if (state != ChoiceState.Idle) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}