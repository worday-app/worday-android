package com.wordayapp.worday.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.wordayapp.worday.data.local.database.dao.DailySessionDao
import com.wordayapp.worday.data.local.database.dao.UserProgressDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class StreakResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userProgressDao: UserProgressDao,
    private val dailySessionDao: DailySessionDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val todayStart = getTodayStartMillis()
            val yesterdayStart = todayStart - ONE_DAY_MILLIS

            // Dün tamamlanmış session var mı?
            val yesterdaySession = dailySessionDao.getSessionByDate(yesterdayStart)
            val didCompleteYesterday = yesterdaySession?.isCompleted == true

            if (!didCompleteYesterday) {
                userProgressDao.resetStreak()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getTodayStartMillis(): Long {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    companion object {
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
        const val WORK_NAME = "streak_reset_worker"

        fun schedule(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<StreakResetWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(calculateDelayUntilMidnight(), TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        private fun calculateDelayUntilMidnight(): Long {
            val now = System.currentTimeMillis()
            val cal = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 1) // gece yarısı + 1 dk
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis - now
        }
    }
}