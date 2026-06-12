package com.ebody.bip.features.auth.domain.model

data class LoginRequest(
    val email: String,
    val password: String
)
