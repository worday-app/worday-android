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
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.LevelTest.route) },
                onSkipToGoal = { navController.navigate(Screen.Goal.route) }
            )
        }

        composable(Screen.LevelTest.route) {
            LevelTestScreen(
                onFinished = { level ->
                    navController.navigate(Screen.Goal.route) {
                        popUpTo(Screen.Onboarding.route)
                    }
                },
                onSkip = {
                    navController.navigate(Screen.Goal.route) {
                        popUpTo(Screen.Onboarding.route)
                    }
                }
            )
        }

        composable(Screen.Goal.route) {
            GoalSelectionScreen(
                onDone = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLearn = { navController.navigate(Screen.Learn.route) }
            )
        }

        composable(Screen.Learn.route) {
            LearnScreen(
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                onNavigateToResult = {
                    navController.navigate(Screen.QuizResult.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.QuizResult.route) {
            QuizResultScreen(
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onRetry = {
                    navController.navigate(Screen.Quiz.route) {
                        popUpTo(Screen.QuizResult.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Wordbook.route) {
            WordbookScreen()
        }

        composable(Screen.Stats.route) {
            // StatsScreen()
        }
    }
}