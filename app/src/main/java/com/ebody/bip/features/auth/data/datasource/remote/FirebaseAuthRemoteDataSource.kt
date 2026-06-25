package com.ebody.bip.features.auth.data.datasource.remote

import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.google.firebase.auth.FirebaseUser

import com.ebody.bip.core.domain.util.Result

interface FirebaseAuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<FirebaseUser?, BipAuthException>
    suspend fun register(email: String, password: String, displayName: String): Result<FirebaseUser?, BipAuthException>
    suspend fun logout(): Result<Unit, BipAuthException>
    suspend fun resetPassword(email: String): Result<Unit, BipAuthException>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit, BipAuthException>
    suspend fun sendEmailVerification(): Result<Unit, BipAuthException>
    suspend fun deleteAccount(): Result<Unit, BipAuthException>
    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String, BipAuthException>
    fun getCurrentUser(): FirebaseUser?
}