package com.ebody.bip.core.presentation.util

import android.annotation.SuppressLint
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@SuppressLint("BatteryLife")
@Composable
fun RequestBatteryOptimizationEffect() {
    val context = LocalContext.current
    val powerManager = context.getSystemService(PowerManager::class.java)

    LaunchedEffect(Unit) {
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }
    }
}