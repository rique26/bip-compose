package com.ebody.bip.core.domain.util

interface EmailValidator {
    fun isValid(email: String): Boolean
}

class AndroidEmailValidator : EmailValidator {
    override fun isValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}