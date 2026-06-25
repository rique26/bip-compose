package com.ebody.bip.features.auth.presentation.register

sealed interface RegisterEvent {
    data class SubmitRegister(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String,
        val birthDate: String,
        val phone: String
    ) : RegisterEvent

    object ClearError : RegisterEvent
}