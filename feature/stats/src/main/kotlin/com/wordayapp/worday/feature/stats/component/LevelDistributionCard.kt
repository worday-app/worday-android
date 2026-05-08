package com.wordayapp.worday.feature.stats.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.domain.model.UserProgress
import com.wordayapp.worday.domain.model.WordLevel
import com.wordayapp.worday.ui.component.WordayCard

private val levelColors = mapOf(
    WordLevel.A1 to Color(0xFF1565C0),
    WordLevel.A2 to Color(0xFF2E7D32),
    WordLevel.B1 to Color(0xFFF57F17),
    WordLevel.B2 to Color(0xFFE65100),
    WordLevel.C1 to Color(0xFF6A1B9A),
    WordLevel.C2 to Color(0xFFB71C1C)
)

@Composable
fun LevelDistributionCard(
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    // Basit dağılım — gerçekte WordRepository'den level bazlı count gelecek
    // Şimdilik mevcut level'a kadar olan seviyeleri göster
    val currentLevelIndex = WordLevel.values().indexOf(progress.level)
    val totalLearned = progress.totalWordsLearned.coerceAtLeast(1)

    WordayCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Level distribution",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            WordLevel.values().forEachIndexed { index, level ->
                if (index > currentLevelIndex) return@forEachIndexed

                val ratio = when (index) {
                    0 -> 0.9f
                    1 -> 0.65f
                    2 -> 0.42f
                    3 -> 0.20f
                    4 -> 0.08f
                    else -> 0.03f
                }
                val count = (totalLearned * ratio).toInt()
                val color = levelColors[level] ?: MaterialTheme.colorScheme.primary

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(color.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = level.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outline)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(ratio)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }

                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}