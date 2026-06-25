package com.ebody.bip.features.auth.domain.model

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long?,
    val lastLoginAt: Long? = null,
    val metadata: UserMetadata = UserMetadata()
)