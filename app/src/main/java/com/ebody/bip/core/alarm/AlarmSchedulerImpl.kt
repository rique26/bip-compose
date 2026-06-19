package com.ebody.bip.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.ebody.bip.features.schedule.domain.AlarmScheduler
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "AlarmDebug"
    }

    override fun schedule(reminder: MedicationReminder) {
        Log.d(TAG, "Scheduling reminder for medication: ${reminder.medication.name} with requestCode: ${reminder.requestCode}, time: ${reminder.time}")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.requestCode,
            buildIntent(reminder),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(buildCalendar(reminder.time), pendingIntent)
    }

    override fun cancel(reminder: MedicationReminder) {
        Log.d(TAG, "Canceling reminder for medication: ${reminder.medication.name} with requestCode: ${reminder.requestCode}")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.requestCode,
            buildIntent(reminder),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "PendingIntent canceled successfully.")
    }

    private fun buildIntent(reminder: MedicationReminder): Intent =
        Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_LABEL", reminder.medication.name)
            putExtra("ALARM_DOSAGE", reminder.dosage)
            putExtra("ALARM_TIME", buildCalendar(reminder.time).timeInMillis)
            putExtra("REQUEST_CODE", reminder.requestCode)
        }.also {
            Log.d(TAG, "Built intent with action: ${it.action} for medication ${reminder.medication.name}")
        }

    private fun buildCalendar(time: LocalTime): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }.also {
            Log.d(TAG, "Calculated Calendar for scheduled time: ${it.time}")
        }
    }

    private fun scheduleExact(calendar: Calendar, pendingIntent: PendingIntent) {
        Log.d(TAG, "Attempting to schedule exact alarm at timeInMillis: ${calendar.timeInMillis}")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "SDK >= 31 and exact alarms are permitted. Calling setExactAndAllowWhileIdle.")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                    )
                } else {
                    Log.w(TAG, "Permissão SCHEDULE_EXACT_ALARM não concedida")
                    // TODO: redirecionar usuário para Settings
                }
            } else {
                Log.d(TAG, "SDK < 31. Calling setExactAndAllowWhileIdle directly.")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException ao agendar alarme", e)
        }
    }
}