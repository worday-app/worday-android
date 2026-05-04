package com.wordayapp.worday

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.wordayapp.worday.data.worker.StreakResetWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class WordayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleStreakResetWorker()
    }

    private fun scheduleStreakResetWorker() {
        val request = PeriodicWorkRequestBuilder<StreakResetWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "streak_reset",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}