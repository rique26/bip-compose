package com.ebody.bip.features.auth.presentation.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccess: Boolean = false
)