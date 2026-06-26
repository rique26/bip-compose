package com.ebody.bip.features.wellbeing.presentation.history

import com.ebody.bip.features.wellbeing.domain.model.TimeFilter

sealed interface HistoryEvent {
    data class OnFilterSelected(val filter: TimeFilter) : HistoryEvent
    data object Refresh : HistoryEvent
}