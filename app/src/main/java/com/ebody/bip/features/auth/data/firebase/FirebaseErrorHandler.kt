package com.ebody.bip.features.auth.data.firebase

import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseErrorHandler @Inject constructor() {

    fun handleException(e: Exception): BipAuthException {
        return when (e) {
            is FirebaseAuthException -> handleAuthException(e)
            is IOException -> BipAuthException.NetworkError
            else -> BipAuthException.Unknown(e.message)
        }
    }
    fun handleAuthException(exception: FirebaseAuthException): BipAuthException {
        return when (exception.errorCode) {
            "ERROR_USER_NOT_FOUND" -> BipAuthException.UserNotFound
            "ERROR_WRONG_PASSWORD" -> BipAuthException.WrongPassword
            "ERROR_EMAIL_ALREADY_IN_USE" -> BipAuthException.EmailAlreadyExists
            "ERROR_WEAK_PASSWORD" -> BipAuthException.WeakPassword
            "ERROR_INVALID_EMAIL" -> BipAuthException.InvalidEmail
            "ERROR_USER_DISABLED" -> BipAuthException.UserDisabled
            "ERROR_TOO_MANY_REQUESTS" -> BipAuthException.TooManyRequests
            "ERROR_NETWORK_REQUEST_FAILED" -> BipAuthException.NetworkError
            "INVALID_LOGIN_CREDENTIALS", "ERROR_INVALID_CREDENTIAL" -> BipAuthException.WrongPassword
            else ->  BipAuthException.Unknown(exception.errorCode)
        }
    }
}