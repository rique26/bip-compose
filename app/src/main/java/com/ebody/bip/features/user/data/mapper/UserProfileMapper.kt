package com.ebody.bip.features.user.data.mapper

import com.ebody.bip.features.user.data.entity.UserProfileEntity
import com.ebody.bip.features.user.domain.model.UserProfile

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        uid = this.uid,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        phone = this.phone,
        email = this.email
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        uid = this.uid,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = this.birthDate,
        phone = this.phone,
        email = this.email
    )
}