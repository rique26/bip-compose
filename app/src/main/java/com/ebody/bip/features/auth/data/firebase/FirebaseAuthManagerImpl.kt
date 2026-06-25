package com.ebody.bip.features.auth.data.firebase

import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.core.domain.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManagerImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val errorHandler: FirebaseErrorHandler
) : FirebaseAuthManager {

    companion object {
        private const val TAG = "FirebaseAuthManager"
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser?, BipAuthException> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = result.user
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(BipAuthException.Unknown("Usuário não encontrado após autenticação."))
            }

        } catch (e: FirebaseAuthException) {
            val mappedError = errorHandler.handleAuthException(e)
            Result.Error(mappedError)
        } catch (e: Exception) {
            Result.Error(BipAuthException.Unknown())
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser?, BipAuthException> {
        return try {
            // Cria o usuário com e-mail e senha
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            // Atualiza o nome de exibição (DisplayName)
            user?.let {
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                it.updateProfile(profileUpdates).await()
            }

            Result.Success(user)
        } catch (e: FirebaseAuthException) {
            Result.Error(errorHandler.handleAuthException(e))
        } catch (e: Exception) {
            Result.Error(BipAuthException.Unknown(e.message ?: "Erro ao registrar"))
        }
    }

    override suspend fun logout(): Result<Unit, BipAuthException> {
        return try {
            firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BipAuthException.Unknown())
        }
    }

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String, BipAuthException> {
        return try {
            val tokenResult = firebaseAuth.currentUser?.getIdToken(forceRefresh)?.await()
            val token = tokenResult?.token

            if (token != null) {
                Result.Success(token)
            } else {
                Result.Error(BipAuthException.Unknown())
            }
        } catch (e: Exception) {
            val mappedError = when(e) {
                is FirebaseAuthException -> errorHandler.handleAuthException(e)
                else -> BipAuthException.Unknown()
            }
            Result.Error(mappedError)
        }
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

}