package com.ebody.bip.features.auth.presentation.login

sealed class LoginUiEvent {
    data class OnEmailChanged(val email: String) : LoginUiEvent()
    data class OnPasswordChanged(val password: String) : LoginUiEvent()
    object OnTogglePasswordVisibility : LoginUiEvent()
    object OnLogin : LoginUiEvent()
}