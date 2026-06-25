package com.ebody.bip.features.schedule.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmDismissReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmDebug"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive triggered. Sending ACTION_DISMISS to AlarmService.")
        context.startService(
            Intent(context, AlarmService::class.java).apply {
                action = AlarmService.ACTION_DISMISS
            }
        ).also {
            Log.d(TAG, "Start service with ACTION_DISMISS called successfully.")
        }
    }
}