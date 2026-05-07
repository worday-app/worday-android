package com.wordayapp.worday.feature.wordbook.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.ui.theme.WordayTheme

@Composable
fun WordbookItem(
    word: Word,
    onUnsave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove from Wordbook") },
            text = { Text("Remove \"${word.english}\" from your saved words?") },
            confirmButton = {
                TextButton(onClick = {
                    onUnsave()
                    showDeleteDialog = false
                }) {
                    Text("Remove", color = WordayTheme.colors.wrong)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol: kelime bilgisi
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = word.english,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    LevelPill(level = word.level.name)
                }
                Text(
                    text = word.turkish,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "×${word.repetitionCount} repetition",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                )
            }

            // Sağ: learned badge veya delete butonu
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (word.isLearned) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(WordayTheme.colors.learned.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "learned",
                            style = MaterialTheme.typography.labelSmall,
                            color = WordayTheme.colors.learned
                        )
                    }
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelPill(level: String) {
    val color = when (level) {
        "A1" -> androidx.compose.ui.graphics.Color(0xFF1565C0)
        "A2" -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
        "B1" -> androidx.compose.ui.graphics.Color(0xFFF57F17)
        "B2" -> androidx.compose.ui.graphics.Color(0xFFE65100)
        "C1" -> androidx.compose.ui.graphics.Color(0xFF6A1B9A)
        "C2" -> androidx.compose.ui.graphics.Color(0xFFB71C1C)
        else -> androidx.compose.ui.graphics.Color.Gray
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = level,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}