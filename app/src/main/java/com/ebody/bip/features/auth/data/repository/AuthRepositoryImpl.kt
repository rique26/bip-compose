package com.ebody.bip.features.auth.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.ebody.bip.core.domain.model.AuthUser
import com.ebody.bip.core.domain.model.BipAuthException
import com.ebody.bip.core.domain.model.UserMetadata
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.data.datasource.local.AuthLocalDataSource
import com.ebody.bip.features.auth.data.datasource.remote.FirebaseAuthRemoteDataSource
import com.ebody.bip.features.auth.data.mapper.toDomainModel
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseAuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun login(email: String, password: String): Result<AuthUser, BipAuthException> {
        return when (val result = remoteDataSource.login(email, password)) {
            is Result.Success -> {
                val user = result.data
                if (user != null) {
                    localDataSource.saveUserId(user.uid)
                    localDataSource.saveUserEmail(user.email ?: "")

                    val tokenResult = remoteDataSource.getIdToken()
                    if (tokenResult is Result.Success) {
                        localDataSource.saveIdToken(tokenResult.data)
                    }

                    Result.Success(user.toDomainModel())
                } else {
                    Result.Error(BipAuthException.UserNotFound)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): Result<AuthUser, BipAuthException> {
        return when (val result = remoteDataSource.register(email, password, displayName)) {
            is Result.Success -> {
                val user = result.data
                if (user != null) {
                    localDataSource.saveUserId(user.uid)
                    localDataSource.saveUserEmail(user.email ?: email)

                    val tokenResult = remoteDataSource.getIdToken()
                    if (tokenResult is Result.Success) {
                        localDataSource.saveIdToken(tokenResult.data)
                    }

                    Result.Success(user.toDomainModel())
                } else {
                    Result.Error(BipAuthException.Unknown("Erro ao processar dados do usuário"))
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }


    override fun getAuthStateFlow(): Flow<AuthUser?> {
        return localDataSource.getUserId().combine(localDataSource.getUserEmail()) { userId, email ->
            if (userId != null) {
                AuthUser(
                    uid = userId,
                    email = email ?: "",
                    displayName = null,
                    photoUrl = null,
                    phoneNumber = null,
                    isEmailVerified = false,
                    createdAt = LocalDateTime.now(),
                    lastLoginAt = null,
                    metadata = UserMetadata()
                )
            } else {
                null
            }
        }
    }

    override suspend fun getCurrentUser(): Result<AuthUser?, BipAuthException> {
        val firebaseUser = remoteDataSource.getCurrentUser()
        return if (firebaseUser != null) {
            Result.Success(firebaseUser.toDomainModel())
        } else {
            Result.Success(null)
        }
    }


    override suspend fun logout(): Result<Unit, BipAuthException> {
        val result = remoteDataSource.logout()
        localDataSource.clearAll()
        return result
    }

    override suspend fun sendEmailVerification(): Result<Unit, BipAuthException> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyEmail(code: String): Result<Unit, BipAuthException> {
        TODO("Not yet implemented")
    }

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String, BipAuthException> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(): Result<String, BipAuthException> {
        TODO("Not yet implemented")
    }

}