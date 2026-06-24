package com.ebody.bip.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderDao: ReminderDao

    companion object {
        private const val TAG = "AlarmDebug"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called with action: ${intent.action}")
        when (intent.action) {

            "${context.packageName}.ALARM_TRIGGER" -> {
                val label = intent.getStringExtra("ALARM_LABEL") ?: "Medicamento"
                val dosage = intent.getStringExtra("ALARM_DOSAGE") ?: ""
                val time = intent.getLongExtra("ALARM_TIME", 0L)
                val requestCode = intent.getIntExtra("REQUEST_CODE", 0)

                Log.d(TAG, "ALARM_TRIGGER received. Label: $label, Dosage: $dosage, Time: $time, RequestCode: $requestCode")

                // Inicia serviço (som + notificação + tela cheia)
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, AlarmService::class.java).apply {
                        action = AlarmService.ACTION_NEW_ALARM   // ← adicione esta linha
                        putExtra("ALARM_LABEL", label)
                        putExtra("ALARM_DOSAGE", dosage)
                    }
                ).also {
                    Log.d(TAG, "Foreground service started successfully from AlarmReceiver.")
                }

                // Reagenda para amanhã automaticamente
                rescheduleForTomorrow(context, intent, time, requestCode)
            }

            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.LOCKED_BOOT_COMPLETED" -> {
                Log.d(TAG, "Boot detectado — restaurando alarmes")
                restoreAlarmsAfterBoot(context)
            }
        }
    }

    private fun rescheduleForTomorrow(
        context: Context,
        originalIntent: Intent,
        time: Long,
        requestCode: Int
    ) {
        // Se time for maior que 0, usa ele como base. Adiciona 1 dia (24h em millis).
        val triggerTime = if (time > 0) time + (24 * 60 * 60 * 1000) else System.currentTimeMillis() + (24 * 60 * 60 * 1000)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = triggerTime
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            originalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleExact(context, calendar, pendingIntent)
        Log.d("AlarmDebug", "Reagendado para amanhã: ${calendar.time} com requestCode: $requestCode")
    }

    private fun restoreAlarmsAfterBoot(context: Context) {
        Log.d(TAG, "restoreAlarmsAfterBoot initiated.")
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminders = reminderDao.getAllActiveReminders().first() // Usando uma chamada de fluxo válida para obter a lista
                Log.d(TAG, "Fetched reminders list. Size: ${reminders.size}")

                reminders.forEach { reminder ->
                    val calendar = buildCalendar(reminder.time, daysFromNow = 0)
                    val intent = buildTriggerIntent(context, reminder)

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminder.requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    scheduleExact(context, calendar, pendingIntent)
                    Log.d(TAG, "Restaurado: medicationId=${reminder.medicationId} às ${calendar.time} com requestCode=${reminder.requestCode}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao restaurar alarmes após boot", e)
            } finally {
                Log.d(TAG, "restoreAlarmsAfterBoot execution finished. Releasing pending result.")
                pendingResult.finish()
            }
        }
    }

    private fun buildTriggerIntent(context: Context, reminder: ReminderEntity): Intent =
        Intent(context, AlarmReceiver::class.java).apply {
            action = "${context.packageName}.ALARM_TRIGGER"
            putExtra("ALARM_LABEL", reminder.medicationId.toString())
            putExtra("ALARM_DOSAGE", reminder.dosage)
            putExtra("ALARM_TIME", reminder.time)
            putExtra("REQUEST_CODE", reminder.requestCode)
        }.also {
            Log.d(TAG, "Built trigger intent for medicationId: ${reminder.medicationId}")
        }

    private fun buildCalendar(timeInMillis: Long, daysFromNow: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        return calendar.apply {
            if (daysFromNow > 0) add(Calendar.DATE, daysFromNow)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (daysFromNow == 0 && before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }.also {
            Log.d(TAG, "Calculated Calendar instance for daysFromNow=$daysFromNow: timeInMillis=${it.timeInMillis}")
        }
    }

    private fun scheduleExact(context: Context, calendar: Calendar, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d(TAG, "Attempting to schedule exact alarm at time: ${calendar.timeInMillis}")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "SDK >= 31 and exact alarms can be scheduled. Calling setExactAndAllowWhileIdle.")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                    )
                } else {
                    Log.w(TAG, "Permissão SCHEDULE_EXACT_ALARM não concedida")
                }
            } else {
                Log.d(TAG, "SDK < 31. Calling setExactAndAllowWhileIdle directly.")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException ao agendar", e)
        }
    }
}