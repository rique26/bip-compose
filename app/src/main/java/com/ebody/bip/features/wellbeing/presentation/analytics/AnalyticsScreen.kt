package com.ebody.bip.features.wellbeing.presentation.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        AnalyticsContent(
            uiState = uiState,
            onFilterSelected = viewModel::loadMetrics,
            modifier = Modifier.padding(paddingValues)
        )
    }
}