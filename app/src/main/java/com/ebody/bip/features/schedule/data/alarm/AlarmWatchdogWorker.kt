package com.ebody.bip.features.schedule.data.alarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.mapper.toDomain
import com.ebody.bip.features.schedule.domain.AlarmScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AlarmWatchdogWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val medicationDao: MedicationDao,
    private val alarmScheduler: AlarmScheduler
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "AlarmDebug"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork started. Executing watchdog to verify and reschedule active alarms.")
        return try {
            val reminders = reminderDao.getAllActiveReminders().first()
            Log.d(TAG, "Active reminders fetched for verification. Count: ${reminders.size}")

            reminders.forEach { reminder ->
                val medEntity = medicationDao.getMedicationById(reminder.medicationId)

                if (medEntity != null) {
                    Log.d(TAG, "Medication found for ID: ${reminder.medicationId}. Proceeding with mapping and scheduling.")
                    val medicationDomain = medEntity.toDomain()
                    val reminderDomain = reminder.toDomain(medicationDomain)
                    alarmScheduler.schedule(reminderDomain)
                } else {
                    Log.w(TAG, "Medication entity is null for medicationId: ${reminder.medicationId}. Skipping scheduling for this reminder.")
                }
            }
            Log.d(TAG, "Reagendados ${reminders.size} alarmes")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Erro no watchdog", e)
            Result.retry()
        }
    }
}