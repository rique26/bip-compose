package com.ebody.bip.features.user.domain.model

data class UserProfile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val phone: String,
    val email: String
)