package com.ebody.bip.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(Intent(context, AlarmService::class.java))
        val closeIntent = Intent(AlarmActivity.ACTION_DISMISS).apply {
            setPackage(context.packageName)
        }
        context.sendBroadcast(closeIntent)
    }
}
