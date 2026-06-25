package com.ebody.bip.features.user.domain.repository

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.BipAuthException

interface UserRepository {
    suspend fun saveUserProfile(
        uid: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        phone: String,
        email: String
    ): Result<Unit, BipAuthException>
}