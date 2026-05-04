package com.wordayapp.worday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wordayapp.worday.navigation.WordayNavGraph
import com.wordayapp.worday.ui.theme.WordayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WordayTheme {
                val navController = rememberNavController()

                // DataStore'dan gelecek — şimdilik false
                val isOnboardingCompleted by remember { mutableStateOf(false) }

                WordayNavGraph(
                    navController = navController,
                    isOnboardingCompleted = isOnboardingCompleted,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}