package com.ebody.bip.features.auth.domain.repository

import com.ebody.bip.core.domain.model.AuthUser
import com.ebody.bip.core.domain.model.BipAuthException
import com.ebody.bip.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    // Auth State Flow
    fun getAuthStateFlow(): Flow<AuthUser?>

    // Current User
    suspend fun getCurrentUser(): Result<AuthUser?, BipAuthException>

    // Login
    suspend fun login(email: String, password: String): Result<AuthUser, BipAuthException>

    // Register
    suspend fun register(email: String, password: String, displayName: String): Result<AuthUser, BipAuthException>

    // Logout
    suspend fun logout(): Result<Unit, BipAuthException>

    // Verify Email
    suspend fun sendEmailVerification(): Result<Unit, BipAuthException>
    suspend fun verifyEmail(code: String): Result<Unit, BipAuthException>

    // Token Management
    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String, BipAuthException>
    suspend fun refreshToken(): Result<String, BipAuthException>

}