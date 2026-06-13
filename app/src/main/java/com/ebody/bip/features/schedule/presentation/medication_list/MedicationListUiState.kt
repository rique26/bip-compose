package com.ebody.bip.features.schedule.presentation.medication_list

sealed class MedicationListUiState {
    object Loading : MedicationListUiState()
    object Success : MedicationListUiState()
    data class Error(val message: String) : MedicationListUiState()
}