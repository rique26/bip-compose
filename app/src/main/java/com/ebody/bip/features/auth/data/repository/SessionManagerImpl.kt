package com.ebody.bip.features.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ebody.bip.features.auth.domain.model.UserSession
import com.ebody.bip.features.auth.domain.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SessionManager {

    private object Keys {
        val ID_TOKEN = stringPreferencesKey("id_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    override fun getUserSession(): Flow<UserSession> = dataStore.data.map { pref ->
        UserSession(
            idToken = pref[Keys.ID_TOKEN],
            refreshToken = pref[Keys.REFRESH_TOKEN],
            userId = pref[Keys.USER_ID],
            email = pref[Keys.USER_EMAIL]
        )
    }

    override suspend fun saveSession(session: UserSession) {
        dataStore.edit { pref ->
            pref[Keys.ID_TOKEN] = session.idToken ?: ""
            pref[Keys.REFRESH_TOKEN] = session.refreshToken ?: ""
            pref[Keys.USER_ID] = session.userId ?: ""
            pref[Keys.USER_EMAIL] = session.email ?: ""
        }
    }

    override suspend fun updateIdToken(newToken: String) {
        dataStore.edit { it[Keys.ID_TOKEN] = newToken }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}