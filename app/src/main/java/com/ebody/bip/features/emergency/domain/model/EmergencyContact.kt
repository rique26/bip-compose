package com.ebody.bip.features.emergency.domain.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isWhatsAppEnabled: Boolean,
    val isSmsEnabled: Boolean
)