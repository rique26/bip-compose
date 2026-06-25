package com.ebody.bip.features.wellbeing.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.wellbeing.domain.usecase.GetAnalyticsMetricsUseCase
import com.ebody.bip.features.wellbeing.presentation.analytics.util.MoodColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsMetricsUseCase: GetAnalyticsMetricsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadMetrics(AnalyticsTimeFilter.Days(7))
    }

    fun loadMetrics(filter: AnalyticsTimeFilter) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is AnalyticsUiState.Success) {
                _uiState.value = currentState.copy(selectedFilter = filter, isChartLoading = true)
            } else {
                _uiState.value = AnalyticsUiState.Loading
            }

            try {
                val result = getAnalyticsMetricsUseCase(filter)

                val items = result.counts.map { (moodName, count) ->
                    ChartItem(
                        label = "Nível $moodName",
                        value = count.toFloat(),
                        colorHex = MoodColors.getColorForMood(moodName),
                        count = count
                    )
                }

                _uiState.value = AnalyticsUiState.Success(
                    totalEntries = result.total,
                    averageMood = result.average,
                    chartItems = items,
                    selectedFilter = filter,
                    isChartLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error("Falha ao carregar métricas.")
            }
        }
    }
}