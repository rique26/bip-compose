package com.ebody.bip.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "${context.packageName}.ALARM_TRIGGER" -> {
                val label  = intent.getStringExtra("ALARM_LABEL") ?: "Medicamento"
                val dosage = intent.getStringExtra("ALARM_DOSAGE") ?: ""

                ContextCompat.startForegroundService(
                    context,
                    Intent(context, AlarmService::class.java).apply {
                        putExtra("ALARM_LABEL", label)
                        putExtra("ALARM_DOSAGE", dosage)
                    }
                )
            }
        }
    }
}
