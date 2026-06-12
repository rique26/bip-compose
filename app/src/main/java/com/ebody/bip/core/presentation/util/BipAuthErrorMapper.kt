package com.ebody.bip.core.presentation.util

import android.content.Context
import com.ebody.bip.R
import com.ebody.bip.core.domain.model.BipAuthException

fun BipAuthException.asString(context: Context): String {
    return when (this) {
        BipAuthException.EmptyEmail -> context.getString(R.string.error_empty_email)
        BipAuthException.InvalidEmail -> context.getString(R.string.error_invalid_email)
        BipAuthException.EmptyPassword -> context.getString(R.string.error_invalid_credentials)
        BipAuthException.WeakPassword -> context.getString(R.string.error_weak_password)
        BipAuthException.UserNotFound -> context.getString(R.string.error_user_not_found)
        BipAuthException.WrongPassword -> context.getString(R.string.error_wrong_password)
        BipAuthException.EmailAlreadyExists -> context.getString(R.string.error_email_exists)
        BipAuthException.UserDisabled -> context.getString(R.string.error_user_disabled)
        BipAuthException.TooManyRequests -> context.getString(R.string.error_too_many_requests)
        BipAuthException.NetworkError -> context.getString(R.string.error_network)
        is BipAuthException.Unknown -> context.getString(R.string.error_unknown)
    }
}