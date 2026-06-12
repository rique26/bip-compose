package com.ebody.bip.core.domain.model

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}