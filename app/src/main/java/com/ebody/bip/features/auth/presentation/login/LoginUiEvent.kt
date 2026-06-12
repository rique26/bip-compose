package com.ebody.bip.features.auth.presentation.login

sealed class LoginUiEvent {
    data class OnEmailChanged(val email: String) : LoginUiEvent()

    object OnLogin : LoginUiEvent()
}