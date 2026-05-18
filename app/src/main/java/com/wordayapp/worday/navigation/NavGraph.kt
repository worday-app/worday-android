package com.wordayapp.worday.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.wordayapp.worday.feature.learn.screen.HomeScreen
import com.wordayapp.worday.feature.learn.screen.LearnScreen
import com.wordayapp.worday.feature.onboarding.screen.GoalSelectionScreen
import com.wordayapp.worday.feature.onboarding.screen.LevelTestScreen
import com.wordayapp.worday.feature.onboarding.screen.WelcomeScreen
import com.wordayapp.worday.feature.quiz.screen.QuizResultScreen
import com.wordayapp.worday.feature.quiz.screen.QuizScreen
import com.wordayapp.worday.feature.quiz.viewmodel.QuizViewModel
import com.wordayapp.worday.feature.stats.screen.StatsScreen
import com.wordayapp.worday.feature.wordbook.screen.WordbookScreen

private val bottomNavScreens = listOf(
    Screen.Home,
    Screen.Learn,
    Screen.Quiz,
    Screen.Wordbook,
    Screen.Stats
)

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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Home -> Icons.Filled.Home
                                        Screen.Learn -> Icons.Filled.School
                                        Screen.Quiz -> Icons.Filled.Quiz
                                        Screen.Wordbook -> Icons.Filled.Book
                                        Screen.Stats -> Icons.Filled.BarChart
                                        else -> Icons.Filled.Home
                                    },
                                    contentDescription = screen.route
                                )
                            },
                            label = {
                                Text(
                                    when (screen) {
                                        Screen.Home -> "Home"
                                        Screen.Learn -> "Learn"
                                        Screen.Quiz -> "Quiz"
                                        Screen.Wordbook -> "Wordbook"
                                        Screen.Stats -> "Stats"
                                        else -> ""
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(Screen.Onboarding.route) {
                WelcomeScreen(
                    onGetStarted = { navController.navigate(Screen.LevelTest.route) },
                    onSkipToGoal = { navController.navigate(Screen.Goal.route) }
                )
            }

            composable(Screen.LevelTest.route) {
                LevelTestScreen(
                    onFinished = {
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
                    onNavigateToLearn = { navController.navigate(Screen.Learn.route) },
                    onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) }
                )
            }

            composable(Screen.Learn.route) {
                LearnScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuiz = { navController.navigate(Screen.Quiz.route) }
                )
            }

            composable(Screen.Quiz.route) { backStackEntry ->
                val quizViewModel: QuizViewModel = hiltViewModel(backStackEntry)
                QuizScreen(
                    viewModel = quizViewModel,
                    onNavigateToResult = { correctCount, totalWords, isPassed ->
                        navController.navigate(
                            Screen.QuizResult.createRoute(correctCount, totalWords, isPassed)
                        )
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.QuizResult.route,
                arguments = listOf(
                    navArgument("correctCount") { type = NavType.IntType },
                    navArgument("totalWords") { type = NavType.IntType },
                    navArgument("isPassed") { type = NavType.BoolType }   // ← YENİ
                )
            ) { backStackEntry ->
                val correctCount = backStackEntry.arguments?.getInt("correctCount") ?: 0
                val totalWords = backStackEntry.arguments?.getInt("totalWords") ?: 1
                val isPassed = backStackEntry.arguments?.getBoolean("isPassed") ?: false
                QuizResultScreen(
                    correctCount = correctCount,
                    totalWords = totalWords,
                    isPassed = isPassed,
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
                StatsScreen()
            }
        }
    }
}