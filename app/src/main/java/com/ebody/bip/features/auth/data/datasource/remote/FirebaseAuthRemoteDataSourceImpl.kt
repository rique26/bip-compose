package com.ebody.bip.features.auth.data.datasource.remote

import com.ebody.bip.features.auth.data.firebase.FirebaseAuthManager
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.core.domain.util.Result
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseAuthRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager
) : FirebaseAuthRemoteDataSource {

    override suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser?, BipAuthException> {
        return firebaseAuthManager.login(email, password)
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser?, BipAuthException> {
        return firebaseAuthManager.register(email, password, displayName)
    }

    override suspend fun logout(): Result<Unit, BipAuthException> {
        return firebaseAuthManager.logout()
    }

    override suspend fun resetPassword(email: String): Result<Unit, BipAuthException> {
        return Result.Success(Unit)
//        return firebaseAuthManager.resetPassword(email)
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit, BipAuthException> {
        return Result.Success(Unit)
//        return firebaseAuthManager.changePassword(currentPassword, newPassword)
    }

    override suspend fun sendEmailVerification(): Result<Unit, BipAuthException> {
        return Result.Success(Unit)
//        return firebaseAuthManager.sendEmailVerification()
    }

    override suspend fun deleteAccount(): Result<Unit, BipAuthException> {
//        return when (val result = firebaseAuthManager.deleteAccount()) {
//            is Result.Success -> {
//                authDataStore.clearAll()
//                Result.Success(Unit)
//            }
//            is Result.Error -> Result.Error(result.exception)
//            Result.Loading -> Result.Loading
//        }
        return Result.Success(Unit)
    }

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String, BipAuthException> {
        return firebaseAuthManager.getIdToken(forceRefresh)
    }

    override fun getCurrentUser() = firebaseAuthManager.getCurrentUser()
}