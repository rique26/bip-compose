package com.ebody.bip

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.ebody.bip.features.schedule.data.alarm.AlarmWatchdogWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BipApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var llmEngine: LlmInferenceEngine

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleAlarmWatchdog()
    }

    private fun scheduleAlarmWatchdog() {
        val watchdog = PeriodicWorkRequestBuilder<AlarmWatchdogWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "alarm_watchdog",
            ExistingPeriodicWorkPolicy.KEEP,
            watchdog
        )
    }

    private fun preloadAiEngine() {
        CoroutineScope(Dispatchers.IO).launch {
            llmEngine.initialize()
        }
    }
}