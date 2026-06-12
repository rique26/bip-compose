package com.ebody.bip.features.auth.data.datasource.local

import kotlinx.coroutines.flow.Flow


interface AuthLocalDataSource {
    fun getIdToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>

    fun getUserId(): Flow<String?>
    fun getUserEmail(): Flow<String?>

    suspend fun saveIdToken(token: String)
    suspend fun saveRefreshToken(token: String)

    suspend fun saveUserId(userId: String)
    suspend fun saveUserEmail(email: String)

    suspend fun clearAll()
}