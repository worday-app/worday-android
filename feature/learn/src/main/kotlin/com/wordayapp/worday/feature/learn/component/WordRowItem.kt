package com.wordayapp.worday.feature.learn.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.domain.model.Word

@Composable
fun WordRowItem(
    word: Word,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.outline
        ),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(12.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = word.turkish,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(levelColor(word.level.name).copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = word.level.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = levelColor(word.level.name)
                )
            }
        }
    }
}

@Composable
private fun levelColor(level: String) = when (level) {
    "A1" -> androidx.compose.ui.graphics.Color(0xFF1565C0)
    "A2" -> androidx.compose.ui.graphics.Color(0xFF2E7D32)
    "B1" -> androidx.compose.ui.graphics.Color(0xFFF57F17)
    "B2" -> androidx.compose.ui.graphics.Color(0xFFE65100)
    "C1" -> androidx.compose.ui.graphics.Color(0xFF6A1B9A)
    "C2" -> androidx.compose.ui.graphics.Color(0xFFB71C1C)
    else -> MaterialTheme.colorScheme.primary
}