package com.ebody.bip.core.domain.model


data class UserMetadata(
    val creationTimestamp: Long = 0,
    val lastSignInTimestamp: Long = 0,
    val isNewUser: Boolean = false
)