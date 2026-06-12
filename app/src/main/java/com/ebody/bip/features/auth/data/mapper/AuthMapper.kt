package com.ebody.bip.features.auth.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.ebody.bip.core.domain.model.AuthUser
import com.ebody.bip.core.domain.model.UserMetadata
import com.google.firebase.auth.FirebaseUser
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun FirebaseUser.toDomainModel(): AuthUser {
    return AuthUser(
        uid = this.uid,
        email = this.email ?: "",
        displayName = this.displayName,
        photoUrl = this.photoUrl?.toString(),
        phoneNumber = this.phoneNumber,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.metadata?.creationTimestamp?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        } ?: LocalDateTime.now(),
        lastLoginAt = this.metadata?.lastSignInTimestamp?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        metadata = UserMetadata(
            creationTimestamp = this.metadata?.creationTimestamp ?: 0,
            lastSignInTimestamp = this.metadata?.lastSignInTimestamp ?: 0
        )
    )
}