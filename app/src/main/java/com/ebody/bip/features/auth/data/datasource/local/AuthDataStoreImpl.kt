package com.ebody.bip.features.auth.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthDataStore {

    private object AuthPreferencesKeys {
        val ID_TOKEN = stringPreferencesKey("id_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    override suspend fun saveIdToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AuthPreferencesKeys.ID_TOKEN] = token
        }
    }

    override fun getIdToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AuthPreferencesKeys.ID_TOKEN]
        }
    }

    override suspend fun clearIdToken() {
        dataStore.edit { preferences ->
            preferences.remove(AuthPreferencesKeys.ID_TOKEN)
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AuthPreferencesKeys.REFRESH_TOKEN] = token
        }
    }

    override fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AuthPreferencesKeys.REFRESH_TOKEN]
        }
    }

    override suspend fun clearRefreshToken() {
        dataStore.edit { preferences ->
            preferences.remove(AuthPreferencesKeys.REFRESH_TOKEN)
        }
    }

    override suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[AuthPreferencesKeys.USER_ID] = userId
        }
    }

    override fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AuthPreferencesKeys.USER_ID]
        }
    }

    override suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[AuthPreferencesKeys.USER_EMAIL] = email
        }
    }

    override fun getUserEmail(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[AuthPreferencesKeys.USER_EMAIL]
        }
    }

    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}