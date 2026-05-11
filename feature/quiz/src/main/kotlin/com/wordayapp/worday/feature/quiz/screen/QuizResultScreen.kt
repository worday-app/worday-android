package com.wordayapp.worday.feature.quiz.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordayapp.worday.feature.quiz.component.QuizResultCard
import com.wordayapp.worday.feature.quiz.viewmodel.QuizViewModel
import com.wordayapp.worday.ui.component.WordayButton
import com.wordayapp.worday.ui.component.WordayOutlinedButton

@Composable
fun QuizResultScreen(
    correctCount: Int,
    totalWords: Int,
    onNavigateHome: () -> Unit,
    onRetry: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        QuizResultCard(
            totalWords = totalWords,
            correctCount = correctCount,
            wrongCount = totalWords - correctCount
        )

        Spacer(modifier = Modifier.weight(1f))

        WordayButton(
            text = "Back to Home",
            onClick = onNavigateHome
        )

        WordayOutlinedButton(
            text = "Try again",
            onClick = onRetry
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}