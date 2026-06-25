package com.ebody.bip.features.auth.domain.repository

import com.ebody.bip.features.auth.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    fun getUserSession(): Flow<UserSession>
    suspend fun saveSession(session: UserSession)
    suspend fun updateIdToken(newToken: String)
    suspend fun clear()
}