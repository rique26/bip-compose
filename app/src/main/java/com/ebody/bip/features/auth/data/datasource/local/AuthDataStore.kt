package com.ebody.bip.features.auth.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface AuthDataStore {
    suspend fun saveIdToken(token: String)
    fun getIdToken(): Flow<String?>
    suspend fun clearIdToken()

    suspend fun saveRefreshToken(token: String)
    fun getRefreshToken(): Flow<String?>
    suspend fun clearRefreshToken()

    suspend fun saveUserId(userId: String)
    fun getUserId(): Flow<String?>

    suspend fun saveUserEmail(email: String)
    fun getUserEmail(): Flow<String?>

    suspend fun clearAll()
}
