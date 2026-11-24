package com.wordayapp.worday

import android.app.Application
import android.util.Log
import androidx.work.WorkManager
import com.wordayapp.worday.data.local.database.WordayDatabase
import com.wordayapp.worday.data.local.database.seed.DatabaseSeeder
import com.wordayapp.worday.data.local.database.seed.SeedState
import com.wordayapp.worday.data.worker.StreakResetWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WordayApplication : Application() {

    @Inject
    lateinit var database: WordayDatabase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            try {
                // Opening the database here also runs pending Room migrations.
                database.openHelper.writableDatabase
                DatabaseSeeder.seed(this@WordayApplication, database)
            } catch (error: Exception) {
                SeedState.markFailed(error)
                Log.e(TAG, "Word catalog initialization failed.", error)
            }
        }

        StreakResetWorker.schedule(WorkManager.getInstance(this))
    }

    private companion object {
        const val TAG = "WordayApplication"
    }
}
