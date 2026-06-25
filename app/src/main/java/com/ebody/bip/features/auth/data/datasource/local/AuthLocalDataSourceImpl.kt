package com.ebody.bip.features.auth.data.datasource.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AuthLocalDataSourceImpl @Inject constructor(
    private val authDataStore: AuthDataStore
) : AuthLocalDataSource {

    override fun getIdToken(): Flow<String?> = authDataStore.getIdToken()
    override fun getRefreshToken(): Flow<String?> {
        TODO("Not yet implemented")
    }

    override fun getUserId(): Flow<String?> = authDataStore.getUserId()
    override fun getUserEmail(): Flow<String?> = authDataStore.getUserEmail()

    override suspend fun saveUserId(userId: String) = authDataStore.saveUserId(userId)
    override suspend fun saveUserEmail(email: String) = authDataStore.saveUserEmail(email)
    override suspend fun saveIdToken(token: String) = authDataStore.saveIdToken(token)
    override suspend fun saveRefreshToken(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearAll() = authDataStore.clearAll()
}