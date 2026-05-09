package com.wordayapp.worday

import android.app.Application
import androidx.work.WorkManager
import com.wordayapp.worday.data.worker.StreakResetWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WordayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StreakResetWorker.schedule(WorkManager.getInstance(this))
    }

}