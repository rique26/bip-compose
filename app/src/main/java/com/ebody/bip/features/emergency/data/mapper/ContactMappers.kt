package com.ebody.bip.features.emergency.data.mapper

import com.ebody.bip.features.emergency.data.local.ContactEntity
import com.ebody.bip.features.emergency.domain.model.EmergencyContact

fun ContactEntity.toDomain(): EmergencyContact {
    return EmergencyContact(
        id = id.toString(),
        name = name,
        phoneNumber = phoneNumber,
        isWhatsAppEnabled = isWhatsAppEnabled,
        isSmsEnabled = isSmsEnabled
    )
}

fun EmergencyContact.toEntity(): ContactEntity {
    return ContactEntity(
        id = id.toLongOrNull() ?: 0L,
        name = name,
        phoneNumber = phoneNumber,
        isWhatsAppEnabled = isWhatsAppEnabled,
        isSmsEnabled = isSmsEnabled
    )
}