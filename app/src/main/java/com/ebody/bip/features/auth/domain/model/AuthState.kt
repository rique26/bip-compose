package com.ebody.bip.features.auth.domain.model

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}