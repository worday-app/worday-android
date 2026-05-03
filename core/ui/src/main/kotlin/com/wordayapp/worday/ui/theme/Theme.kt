package com.wordayapp.worday.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = WarmPrimary,
    onPrimary = WarmOnPrimary,
    secondary = WarmSecondary,
    background = WarmBackground,
    surface = WarmSurface,
    onBackground = WarmOnBackground,
    onSurface = WarmOnBackground,
    outline = WarmOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = WarmPrimaryDark,
    onPrimary = WarmOnPrimaryDark,
    secondary = WarmSecondaryDark,
    background = WarmBackgroundDark,
    surface = WarmSurfaceDark,
    onBackground = WarmOnBackgroundDark,
    onSurface = WarmOnBackgroundDark,
    outline = WarmOutlineDark
)

// Semantik renklere Compose içinden erişim için
data class WordaySemanticColors(
    val learned: Color,
    val wrong: Color,
    val streak: Color
)

val LocalWordayColors = staticCompositionLocalOf {
    WordaySemanticColors(
        learned = ColorLearned,
        wrong = ColorWrong,
        streak = ColorStreak
    )
}

@Composable
fun WordayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val semanticColors = if (darkTheme) {
        WordaySemanticColors(
            learned = ColorLearnedDark,
            wrong = ColorWrongDark,
            streak = ColorStreak
        )
    } else {
        WordaySemanticColors(
            learned = ColorLearned,
            wrong = ColorWrong,
            streak = ColorStreak
        )
    }

    CompositionLocalProvider(LocalWordayColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WordayTypography,
            shapes = WordayShapes,
            content = content
        )
    }
}

// Kullanım kolaylığı için extension
object WordayTheme {
    val colors: WordaySemanticColors
        @Composable get() = LocalWordayColors.current
}