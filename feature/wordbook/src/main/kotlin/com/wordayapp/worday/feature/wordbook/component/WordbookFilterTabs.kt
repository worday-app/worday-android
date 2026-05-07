package com.wordayapp.worday.feature.wordbook.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.feature.wordbook.viewmodel.WordbookFilter

@Composable
fun WordbookFilterTabs(
    activeFilter: WordbookFilter,
    allCount: Int,
    learningCount: Int,
    learnedCount: Int,
    onFilterChange: (WordbookFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        FilterTab(
            label = "All ($allCount)",
            selected = activeFilter == WordbookFilter.All,
            onClick = { onFilterChange(WordbookFilter.All) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            label = "Learning ($learningCount)",
            selected = activeFilter == WordbookFilter.Learning,
            onClick = { onFilterChange(WordbookFilter.Learning) },
            modifier = Modifier.weight(1f)
        )
        FilterTab(
            label = "Learned ($learnedCount)",
            selected = activeFilter == WordbookFilter.Learned,
            onClick = { onFilterChange(WordbookFilter.Learned) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FilterTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(9.dp),
        color = if (selected) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.padding(vertical = 7.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}