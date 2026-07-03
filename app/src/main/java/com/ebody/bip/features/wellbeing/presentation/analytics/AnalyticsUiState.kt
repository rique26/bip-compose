package com.ebody.bip.features.wellbeing.presentation.analytics

sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data class Success(
        val totalEntries: Int,
        val averageMood: String,
        val chartItems: List<ChartItem>,
        val selectedFilter: AnalyticsTimeFilter,
        val isChartLoading: Boolean = false,
        val isGeneratingSummary: Boolean = false,
        val clinicalSummary: String? = null
    ) : AnalyticsUiState

    data class Error(val message: String) : AnalyticsUiState
}

data class ChartItem(
    val label: String,
    val value: Float,
    val colorHex: String,
    val count: Int
)