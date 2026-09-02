package com.ebody.bip.features.wellbeing.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import com.ebody.bip.features.wellbeing.domain.usecase.GenerateClinicalSummaryUseCase
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
    private val getAnalyticsMetricsUseCase: GetAnalyticsMetricsUseCase,
    private val generateClinicalSummaryUseCase: GenerateClinicalSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private var currentPeriodRecords: List<MoodEntry> = emptyList()

    init {
        loadMetrics(AnalyticsTimeFilter.Days(7))
    }

    fun loadMetrics(filter: AnalyticsTimeFilter) {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is AnalyticsUiState.Success) {
                _uiState.value = currentState.copy(
                    selectedFilter = filter,
                    isChartLoading = true,
                    clinicalSummary = null // Reseta o resumo antigo mudando o período
                )
            } else {
                _uiState.value = AnalyticsUiState.Loading
            }

            try {
                val result = getAnalyticsMetricsUseCase(filter)

                currentPeriodRecords = result.records

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
                    isChartLoading = false,
                    clinicalSummary = null
                )
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error("Falha ao carregar métricas.")
            }
        }
    }

    fun generateClinicalSummary() {
        val currentState = _uiState.value as? AnalyticsUiState.Success ?: return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isGeneratingSummary = true)

            val summaryText = generateClinicalSummaryUseCase(
                records = currentPeriodRecords,
                filterLabel = toDisplayLabel(currentState.selectedFilter)
            )

            // Atualiza o estado mantendo os dados intactos e adicionando o relatório
            _uiState.value = (_uiState.value as AnalyticsUiState.Success).copy(
                isGeneratingSummary = false,
                clinicalSummary = summaryText
            )
        }
    }

    fun clearClinicalSummary() {
        val currentState = _uiState.value as? AnalyticsUiState.Success ?: return
        _uiState.value = currentState.copy(clinicalSummary = null)
    }

    private fun toDisplayLabel(filter: AnalyticsTimeFilter): String {
        return when (filter) {
            is AnalyticsTimeFilter.Days -> "Últimos ${filter.amount} dias"
            is AnalyticsTimeFilter.Custom -> "Período Personalizado"
        }
    }
}