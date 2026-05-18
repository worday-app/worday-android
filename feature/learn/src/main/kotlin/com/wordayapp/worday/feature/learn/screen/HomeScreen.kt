package com.wordayapp.worday.feature.learn.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.common.ui.UiState
import com.wordayapp.worday.domain.model.Word
import com.wordayapp.worday.feature.learn.component.DailyProgressCard
import com.wordayapp.worday.feature.learn.component.StreakBadge
import com.wordayapp.worday.feature.learn.component.WordRowItem
import com.wordayapp.worday.feature.learn.viewmodel.HomeViewModel
import com.wordayapp.worday.ui.component.WordayButton
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good morning 👋",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ready to learn?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )
            }
            val progress = state.progressState
            if (progress is UiState.Success) {
                StreakBadge(streak = progress.data.currentStreak)
            }
        }

        // Günlük progress
        DailyProgressCard(
            studied = state.todayStudied,
            goal = state.dailyGoal
        )
        Text("Kelime sayısı: ${state.todayWords.size}")
        Text("Index: ${state.currentCardIndex}")

        // Flashcard veya Quiz butonu
        // Flashcard / Quiz / Tamamlandı alanı
        when {
            state.isWordsLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            state.todayQuizCompleted -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🎉", fontSize = 32.sp)
                        Text(
                            text = "Bugünü tamamladın!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Yarın yeni kelimeler seni bekliyor.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            state.allWordsSeen -> {
                WordayButton(
                    text = "Quiz'e Başla 🎯",
                    onClick = onNavigateToQuiz
                )
            }

            state.currentWord != null -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Bugünün Kelimeleri",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    key(state.currentCardIndex) {
                        FlashcardItem(
                            word = state.currentWord!!,
                            onSwiped = { viewModel.onCardSwiped() }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("← Bilmiyorum", style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC0392B).copy(alpha = 0.6f))
                        Text("${state.currentCardIndex + 1} / ${state.todayWords.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        Text("Biliyorum →", style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2D7A50).copy(alpha = 0.6f))
                    }
                }
            }
        }

        // Yarının kelimeleri — kilitli
        if (state.tomorrowWords.isNotEmpty()) {
            Text(
                text = "Yarının Kelimeleri",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Box {
                Column(
                    modifier = Modifier.blur(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.tomorrowWords.take(4).forEach { word ->
                        WordRowItem(word = word)
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🔒", fontSize = 28.sp)
                        Text(
                            text = "Bugünü tamamla",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Due for review
        if (state.reviewWords.isNotEmpty()) {
            Text(
                text = "Tekrar Zamanı",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.reviewWords.forEach { word ->
                    WordRowItem(word = word)
                }
            }
        }

        // Stat grid
        val progress = state.progressState
        if (progress is UiState.Success) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    value = progress.data.totalWordsLearned.toString(),
                    label = "words learned",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${(progress.data.accuracyRate * 100).toInt()}%",
                    label = "accuracy",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FlashcardItem(
    word: Word,
    onSwiped: (knew: Boolean) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val swipeThreshold = 120f

    val swipeProgress = (offsetX.value / swipeThreshold).coerceIn(-1f, 1f)
    val isRight = offsetX.value > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer { rotationZ = offsetX.value / 25f }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (abs(offsetX.value) >= swipeThreshold) {
                                val knew = offsetX.value > 0
                                offsetX.animateTo(
                                    targetValue = if (knew) 1200f else -1200f,
                                    animationSpec = tween(durationMillis = 220)
                                )
                                onSwiped(knew)
                            } else {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch { offsetX.animateTo(0f) }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            }
    ) {
        val borderColor = when {
            swipeProgress > 0.15f -> Color(0xFF2D7A50).copy(alpha = swipeProgress)
            swipeProgress < -0.15f -> Color(0xFFC0392B).copy(alpha = -swipeProgress)
            else -> MaterialTheme.colorScheme.outline
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = if (abs(swipeProgress) > 0.15f) 2.dp else 1.dp,
                color = borderColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = word.english,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                if (word.ipa.isNotBlank()) {
                    Text(
                        text = word.ipa,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = word.turkish,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                if (word.definition.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = word.definition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Swipe göstergesi
        val indicatorAlpha = ((abs(swipeProgress) - 0.15f) / 0.85f).coerceIn(0f, 1f)
        if (indicatorAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = if (isRight) Alignment.TopStart else Alignment.TopEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = (if (isRight) Color(0xFF2D7A50) else Color(0xFFC0392B))
                        .copy(alpha = indicatorAlpha * 0.9f)
                ) {
                    Text(
                        text = if (isRight) "✓ Biliyorum" else "✗ Bilmiyorum",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
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