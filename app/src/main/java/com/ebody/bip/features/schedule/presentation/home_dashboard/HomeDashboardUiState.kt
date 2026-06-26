package com.ebody.bip.features.schedule.presentation.home_dashboard

import com.ebody.bip.features.schedule.domain.model.MedicationReminder

data class HomeDashboardUiState(
    val reminders: List<MedicationReminder> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)