package com.ebody.bip.features.auth.domain.usecase

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.AuthUser
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import com.ebody.bip.features.user.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        phone: String
    ): Result<AuthUser, BipAuthException> {

        // Cria o usuário no Firebase Auth
        val fullName = "$firstName $lastName".trim()
        val authResult = authRepository.register(email, password, fullName)

        if (authResult is Result.Error) {
            return authResult
        }

        //  Obtém o UID gerado
        val user = (authResult as Result.Success).data

        //  Persiste no Firestore
        val profileResult = userRepository.saveUserProfile(
            uid = user.uid,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            phone = phone,
            email = email
        )

        //  Trata falha na persistência
        return when (profileResult) {
            is Result.Success -> Result.Success(user)
            is Result.Error -> {
                Result.Error(profileResult.error)
            }
        }
    }
}