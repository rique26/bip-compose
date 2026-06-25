package com.ebody.bip.features.user.data.entity

import com.google.firebase.database.PropertyName

data class UserProfileEntity(
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("first_name") @set:PropertyName("first_name")
    var firstName: String = "",

    @get:PropertyName("last_name") @set:PropertyName("last_name")
    var lastName: String = "",

    @get:PropertyName("birth_date") @set:PropertyName("birth_date")
    var birthDate: String = "",

    @get:PropertyName("phone") @set:PropertyName("phone")
    var phone: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)