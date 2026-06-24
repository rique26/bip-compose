package com.ebody.bip.features.emergency.presentation.emergency

sealed interface EmergencyUiState {
    data object Initial : EmergencyUiState
    data object Loading : EmergencyUiState
    data object Success : EmergencyUiState
    data class Error(val message: String) : EmergencyUiState
}