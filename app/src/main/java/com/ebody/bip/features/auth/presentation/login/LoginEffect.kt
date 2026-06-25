package com.ebody.bip.features.auth.presentation.login

import com.ebody.bip.features.auth.domain.model.BipAuthException

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class ShowError(val error: BipAuthException) : LoginEffect
}