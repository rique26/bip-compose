package com.ebody.bip

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ebody.bip.navigation.NavGraph
import com.ebody.bip.core.presentation.theme.AppTheme
import com.ebody.bip.core.util.RequestBatteryOptimizationEffect
import com.ebody.bip.features.auth.presentation.viewmodel.AuthSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.Transparent.toArgb(), Color.White.toArgb(),
            )
        )
        checkExactAlarmPermission(this)
        setContent {
            AppTheme {
                RequestBatteryOptimizationEffect()
                BipApp()
            }
        }
    }
}

@Composable
@Preview
fun BipApp(viewModel: AuthSharedViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authStatus by viewModel.isAuthenticated.collectAsState()

    if (authStatus == null) return

    NavGraph(
        navController,
        isAuthenticated = authStatus == true
    )
}

private fun checkExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )
        }
    }
}