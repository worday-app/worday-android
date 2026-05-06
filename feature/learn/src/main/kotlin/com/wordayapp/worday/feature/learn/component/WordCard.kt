package com.wordayapp.worday.feature.learn.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.wordayapp.worday.domain.model.Word

@Composable
fun WordCard(
    word: Word,
    onTtsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: level pill + tür + TTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                word.level.name,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                word.type.name.lowercase(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
                IconButton(onClick = onTtsClick) {
                    Text(text = "🔊", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Kelime + IPA
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = word.ipa,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // Türkçe
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Turkish",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                )
                Text(
                    text = word.turkish,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Definition
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Definition",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                )
                Text(
                    text = word.definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Örnek cümle
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Example",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                    )
                    Text(
                        text = word.exampleSentence1,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}