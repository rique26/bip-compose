package com.ebody.bip.features.schedule.data.alarm

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebody.bip.core.presentation.theme.AppTheme

class AlarmActivity : ComponentActivity() {

    companion object {
        const val ACTION_DISMISS = "com.ebody.bip.ACTION_DISMISS_ALARM"
        private const val TAG = "AlarmDebug"
    }

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Dismiss broadcast received in AlarmActivity. Finishing activity.")
            finish()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called.")

        // Acende tela e mostra sobre lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d(TAG, "SDK >= O_MR1. Setting setShowWhenLocked and setTurnScreenOn.")
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            Log.d(TAG, "SDK < O_MR1. Adding deprecated window flags.")
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        // Bloqueia o botão voltar — usuário só pode dispensar
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back button pressed but intercepted (disabled).")
                // não faz nada — força o usuário a clicar em Dispensar
            }
        })

        // Registra receiver para fechar quando dispensar pela notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "Registering dismiss receiver with RECEIVER_NOT_EXPORTED.")
            registerReceiver(dismissReceiver, IntentFilter(ACTION_DISMISS), RECEIVER_NOT_EXPORTED)
        } else {
            Log.d(TAG, "Registering dismiss receiver standard.")
            registerReceiver(dismissReceiver, IntentFilter(ACTION_DISMISS))
        }

        val label  = intent.getStringExtra("ALARM_LABEL") ?: "Medicamento"
        val dosage = intent.getStringExtra("ALARM_DOSAGE") ?: ""
        Log.d(TAG, "Alarm data retrieved - Label: $label, Dosage: $dosage")

        setContent {
            AppTheme {
                AlarmScreen(
                    label = label,
                    dosage = dosage,
                    onDismiss = {
                        Log.d(TAG, "Alarm dismissed by UI button. Stopping AlarmService and finishing activity.")
                        stopService(Intent(this, AlarmService::class.java))
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called. Unregistering dismiss receiver.")
        runCatching { unregisterReceiver(dismissReceiver) }
        super.onDestroy()
    }
}

@Composable
private fun AlarmScreen(
    label: String,
    dosage: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⏰", fontSize = 80.sp)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Hora do medicamento!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            if (dosage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = dosage,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(56.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "✓  Dispensar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}