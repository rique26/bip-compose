package com.ebody.bip.core.data.remote.firebase

import com.ebody.bip.core.domain.model.BipAuthException
import com.ebody.bip.core.domain.util.Result
import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthManager {
    suspend fun login(email: String, password: String) : Result<FirebaseUser?, BipAuthException>
    suspend fun logout(): Result<Unit, BipAuthException>
    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String, BipAuthException>
    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Boolean

}