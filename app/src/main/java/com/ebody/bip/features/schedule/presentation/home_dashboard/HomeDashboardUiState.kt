package com.ebody.bip.features.schedule.presentation.home_dashboard

import com.ebody.bip.features.schedule.domain.model.MedicationReminder

data class HomeDashboardUiState(
    val isLoading: Boolean = false,
    val reminders: List<MedicationReminder> = emptyList(),
    val error: String? = null
)