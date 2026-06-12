package com.ebody.bip.core.domain.model

sealed class BipAuthException : Exception() {
    object EmptyEmail : BipAuthException()
    object InvalidEmail : BipAuthException()
    object EmptyPassword : BipAuthException()
    object WeakPassword : BipAuthException()
    object UserNotFound : BipAuthException()
    object WrongPassword : BipAuthException()
    object EmailAlreadyExists : BipAuthException()
    object UserDisabled : BipAuthException()
    object TooManyRequests : BipAuthException()
    object NetworkError : BipAuthException()
    data class Unknown(val errorMessage: String? = null) : BipAuthException()
}