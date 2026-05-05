package com.wordayapp.worday.feature.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordayapp.worday.ui.component.WordayButton
import com.wordayapp.worday.ui.component.WordayOutlinedButton

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSkipToGoal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "W", fontSize = 40.sp, color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Worday",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Learn 5000+ English words\nwith spaced repetition",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Feature list
        FeatureItem(emoji = "🧠", text = "SM-2 algorithm personalizes your review schedule")
        Spacer(modifier = Modifier.height(12.dp))
        FeatureItem(emoji = "🔥", text = "Daily streaks keep you motivated")
        Spacer(modifier = Modifier.height(12.dp))
        FeatureItem(emoji = "📶", text = "Works fully offline, always")

        Spacer(modifier = Modifier.weight(1f))

        WordayButton(
            text = "Get started",
            onClick = onGetStarted
        )

        Spacer(modifier = Modifier.height(10.dp))

        WordayOutlinedButton(
            text = "I already know my level",
            onClick = onSkipToGoal
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FeatureItem(emoji: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
    }
}