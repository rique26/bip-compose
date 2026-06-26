package com.ebody.bip.features.schedule.presentation.home_dashboard

import com.ebody.bip.features.schedule.domain.model.MedicationReminder

sealed interface HomeDashboardEvent {
    data class DeleteReminder(val reminder: MedicationReminder) : HomeDashboardEvent
    object Refresh : HomeDashboardEvent
}