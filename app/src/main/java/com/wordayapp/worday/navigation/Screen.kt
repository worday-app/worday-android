package com.wordayapp.worday.navigation

sealed class Screen(val route: String) {
    // Onboarding
    object Onboarding : Screen("onboarding")
    object LevelTest : Screen("level_test")

    object Goal : Screen("goal")

    // Ana ekranlar
    object Home : Screen("home")
    object Learn : Screen("learn")
    object Quiz : Screen("quiz")
    object Wordbook : Screen("wordbook")
    object Stats : Screen("stats")

    // Detail
    object WordDetail : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: Int) = "word_detail/$wordId"
    }

    object QuizResult : Screen("quiz_result")
}