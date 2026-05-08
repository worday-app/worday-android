package com.wordayapp.worday.feature.stats.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.domain.model.UserProgress

@Composable
fun StatGrid(
    progress: UserProgress,
    dueCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCell(
                value = progress.totalWordsLearned.toString(),
                label = "words learned",
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = "${(progress.accuracyRate * 100).toInt()}%",
                label = "accuracy rate",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCell(
                value = dueCount.toString(),
                label = "due for review",
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = progress.level.name,
                label = "current level",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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