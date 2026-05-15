package com.wordayapp.worday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.wordayapp.worday.data.local.datastore.UserPreferencesDataStore
import com.wordayapp.worday.navigation.WordayNavGraph
import com.wordayapp.worday.ui.theme.WordayTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isReady = false
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            WordayTheme {
                val isOnboardingCompleted by dataStore.isOnboardingCompleted
                    .collectAsState(initial = null)

                if (isOnboardingCompleted != null) {
                    isReady = true
                    WordayNavGraph(
                        navController = rememberNavController(),
                        isOnboardingCompleted = isOnboardingCompleted!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
