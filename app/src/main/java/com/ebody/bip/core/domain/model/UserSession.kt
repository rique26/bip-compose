package com.ebody.bip.core.domain.model

data class UserSession(
    val idToken: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val email: String? = null
)