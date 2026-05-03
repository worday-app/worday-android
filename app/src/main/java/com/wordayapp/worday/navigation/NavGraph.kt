package com.wordayapp.worday.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun WordayNavGraph(
    navController: NavHostController,
    isOnboardingCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val startDestination = if (isOnboardingCompleted) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            // OnboardingScreen() — feature:onboarding hazır olunca eklenecek
        }

        composable(Screen.Home.route) {
            // HomeScreen() — feature:learn hazır olunca eklenecek
        }

        composable(Screen.Learn.route) {
            // LearnScreen()
        }

        composable(Screen.Quiz.route) {
            // QuizScreen()
        }

        composable(Screen.Wordbook.route) {
            // WordbookScreen()
        }

        composable(Screen.Stats.route) {
            // StatsScreen()
        }
    }
}