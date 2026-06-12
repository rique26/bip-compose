package com.ebody.bip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ebody.bip.core.presentation.navigation.NavGraph
import com.ebody.bip.core.presentation.theme.AppTheme
import com.ebody.bip.core.presentation.util.RequestBatteryOptimizationEffect
import com.ebody.bip.features.auth.presentation.viewmodel.AuthSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    if (authStatus == null) {
        return
    }

    NavGraph(
        navController,
        isAuthenticated = authStatus == true
    )
}