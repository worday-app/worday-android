package com.wordayapp.worday.feature.wordbook.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.feature.wordbook.component.WordbookFilterTabs
import com.wordayapp.worday.feature.wordbook.component.WordbookItem
import com.wordayapp.worday.feature.wordbook.viewmodel.WordbookViewModel
import com.wordayapp.worday.ui.component.LoadingIndicator

@Composable
fun WordbookScreen(
    viewModel: WordbookViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Wordbook",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Search
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = {
                Text(
                    "Search saved words...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Filter tabs
        WordbookFilterTabs(
            activeFilter = state.activeFilter,
            allCount = state.allCount,
            learningCount = state.learningCount,
            learnedCount = state.learnedCount,
            onFilterChange = { viewModel.onFilterChange(it) }
        )

        // Liste
        if (state.filteredWords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.searchQuery.isNotBlank()) "No results found"
                    else "No saved words yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(
                    items = state.filteredWords,
                    key = { it.id }
                ) { word ->
                    WordbookItem(
                        word = word,
                        onUnsave = { viewModel.onUnsaveWord(word.id) }
                    )
                }
            }
        }
    }
}