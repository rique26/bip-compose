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
        return when {
            request.email.isBlank() -> Result.Error(BipAuthException.EmptyEmail)
            request.password.isBlank() -> Result.Error(BipAuthException.EmptyPassword)
            !emailValidator.isValid(request.email) -> Result.Error(BipAuthException.InvalidEmail)
            else -> repository.login(request.email, request.password)
        }
    }
}