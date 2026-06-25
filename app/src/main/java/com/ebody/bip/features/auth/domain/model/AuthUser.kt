package com.ebody.bip.features.auth.domain.model

import java.time.LocalDateTime

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime? = null,
    val metadata: UserMetadata = UserMetadata()
)