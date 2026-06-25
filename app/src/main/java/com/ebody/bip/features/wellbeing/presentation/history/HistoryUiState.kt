package com.ebody.bip.features.wellbeing.presentation.history

import com.ebody.bip.features.wellbeing.domain.model.MoodEntry

data class HistoryUiState(
    val records: List<MoodEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)