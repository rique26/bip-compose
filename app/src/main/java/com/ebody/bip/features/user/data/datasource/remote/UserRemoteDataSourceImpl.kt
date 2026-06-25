package com.ebody.bip.features.user.data.datasource.remote

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.user.data.entity.UserProfileEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    override suspend fun saveProfile(userProfile: UserProfileEntity): Result<Unit, BipAuthException> {
        return try {
            firestore.collection("users")
                .document(userProfile.uid)
                .set(userProfile)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BipAuthException.Unknown(e.message ?: "Erro desconhecido ao salvar perfil"))
        }
    }
}