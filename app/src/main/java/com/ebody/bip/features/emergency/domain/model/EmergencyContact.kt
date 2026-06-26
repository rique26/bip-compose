package com.ebody.bip.features.emergency.domain.model

data class EmergencyContact(
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val isWhatsAppEnabled: Boolean,
    val isSmsEnabled: Boolean
)