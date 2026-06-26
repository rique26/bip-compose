package com.ebody.bip.features.schedule.presentation.home_dashboard

import com.ebody.bip.core.domain.intelligence.model.BipMascotExpression
import com.ebody.bip.core.domain.intelligence.model.BipRiskLevel
import com.ebody.bip.features.schedule.domain.model.MedicationReminder

data class HomeDashboardUiState(
    val reminders: List<MedicationReminder> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val riskLevel: BipRiskLevel = BipRiskLevel.STABLE,
    val bipMessage: String = "Continue mantendo sua rotina em dia! Estou de olho para te ajudar.",
    val mascotExpression: BipMascotExpression = BipMascotExpression.HAPPY
)