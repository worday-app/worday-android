package com.wordayapp.worday.feature.onboarding.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.feature.onboarding.viewmodel.OnboardingViewModel
import com.wordayapp.worday.ui.component.WordayButton

private val GOALS = listOf(
    Triple(5, "words/day", "Casual"),
    Triple(10, "words/day", "Recommended"),
    Triple(15, "words/day", "Serious"),
    Triple(20, "words/day", "Intense")
)

@Composable
fun GoalSelectionScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Set your daily goal",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "How many words do you want to learn each day?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // 2x2 grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GOALS.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (goal, unit, label) ->
                        GoalCard(
                            goal = goal,
                            unit = unit,
                            label = label,
                            selected = state.selectedGoal == goal,
                            isRecommended = goal == 10,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setGoal(goal) }
                        )
                    }
                }
            }
        }

        // Preview card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "With ${state.selectedGoal} words/day you'll learn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "~${state.selectedGoal * 30} words in a month",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        WordayButton(
            text = if (state.isCompleting) "Setting up..." else "Start learning",
            enabled = !state.isCompleting,
            onClick = { viewModel.completeOnboarding(onDone) }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GoalCard(
    goal: Int,
    unit: String,
    label: String,
    selected: Boolean,
    isRecommended: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$goal",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isRecommended && selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        }
    }
}