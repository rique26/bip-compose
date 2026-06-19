package com.ebody.bip.features.schedule.presentation.medication_selection

sealed class MedicationSelectionUiState {
    object Loading : MedicationSelectionUiState()
    object Success : MedicationSelectionUiState()
    data class Error(val message: String) : MedicationSelectionUiState()
}