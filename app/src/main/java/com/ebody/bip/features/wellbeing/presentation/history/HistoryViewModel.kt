package com.ebody.bip.features.wellbeing.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.wellbeing.domain.model.TimeFilter
import com.ebody.bip.features.wellbeing.domain.usecase.GetMoodHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getMoodHistoryUseCase: GetMoodHistoryUseCase
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.LAST_30_DAYS)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        getMoodHistoryUseCase(),
        _selectedFilter
    ) { entries, filter ->
        val now = LocalDateTime.now()

        val filteredList = when (filter) {
            TimeFilter.LAST_30_DAYS -> {
                entries.filter { it.dateTime.isAfter(now.minusDays(30)) }
            }
            TimeFilter.PREVIOUS_MONTH -> {
                val startOfCurrentMonth = now.withDayOfMonth(1).toLocalDate()
                val startOfPreviousMonth = startOfCurrentMonth.minusMonths(1)
                val endOfPreviousMonth = startOfCurrentMonth.minusDays(1)

                entries.filter {
                    val date = it.dateTime.toLocalDate()
                    (date.isAfter(startOfPreviousMonth) || date.isEqual(startOfPreviousMonth)) &&
                            (date.isBefore(endOfPreviousMonth) || date.isEqual(endOfPreviousMonth))
                }
            }
            TimeFilter.ALL_TIME -> entries
        }

        HistoryUiState(records = filteredList, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun onFilterSelected(filter: TimeFilter) {
        _selectedFilter.value = filter
    }
}