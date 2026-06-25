package com.ebody.bip.features.user.data.repository

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.user.data.datasource.remote.UserRemoteDataSource
import com.ebody.bip.features.user.data.mapper.toEntity
import com.ebody.bip.features.user.domain.model.UserProfile
import com.ebody.bip.features.user.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun saveUserProfile(
        uid: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        phone: String,
        email: String
    ): Result<Unit, BipAuthException> {

        val userProfile = UserProfile(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            phone = phone,
            email = email
        )

        // Converte domínio para a Entity do Firestore usando o mapper e envia para salvar
        return remoteDataSource.saveProfile(userProfile.toEntity())
    }
}