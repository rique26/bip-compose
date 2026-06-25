package com.ebody.bip.features.auth.domain.usecase

import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.auth.domain.model.AuthUser
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import com.ebody.bip.features.auth.util.EmailValidator
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.LoginRequest
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val emailValidator: EmailValidator
) {
    suspend operator fun invoke(request: LoginRequest): Result<AuthUser, BipAuthException> {
        val trimmedEmail = request.email.trim()
        val trimmedPassword = request.password.trim()

        return when {
            trimmedEmail.isBlank() -> Result.Error(BipAuthException.EmptyEmail)
            trimmedPassword.isBlank() -> Result.Error(BipAuthException.EmptyPassword)
            !emailValidator.isValid(trimmedEmail) -> Result.Error(BipAuthException.InvalidEmail)
            else -> repository.login(trimmedEmail, trimmedPassword)
        }
    }
}