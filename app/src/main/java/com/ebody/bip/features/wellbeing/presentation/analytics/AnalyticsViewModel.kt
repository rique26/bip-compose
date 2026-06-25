package com.ebody.bip.features.wellbeing.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadMetrics(AnalyticsTimeFilter.Days(7))
    }

    fun loadMetrics(filter: AnalyticsTimeFilter) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is AnalyticsUiState.Success) {
                _uiState.value = currentState.copy(
                    selectedFilter = filter,
                    isChartLoading = true
                )
            } else {
                _uiState.value = AnalyticsUiState.Loading
            }

            try {
                kotlinx.coroutines.delay(300)

                val items = listOf(
                    ChartItem(label = "Ótimo", value = 2f, colorHex = "#1DB954", count = 2),
                    ChartItem(label = "Bem", value = 4f, colorHex = "#FFD13B", count = 4),
                    ChartItem(label = "Estranho", value = 1f, colorHex = "#9C27B0", count = 1),
                    ChartItem(label = "Mal", value = 1f, colorHex = "#E53935", count = 1)
                )

                _uiState.value = AnalyticsUiState.Success(
                    totalEntries = items.sumOf { it.count },
                    averageMood = "Bem",
                    chartItems = items,
                    selectedFilter = filter,
                    isChartLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error("Não foi possível carregar as estatísticas.")
            }
        }
    }
}