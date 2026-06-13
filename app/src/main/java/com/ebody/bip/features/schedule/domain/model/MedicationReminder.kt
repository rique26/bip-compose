package com.ebody.bip.features.schedule.domain.model

data class MedicationReminder(
    val id: Long = 0,
    val medicationId: String, // Vincula ao medicamento escolhido
    val dosage: String,       // ex: "2 comprimidos"
    val time: String,         // ex: "08:00"
    val createdAt: Long,
    val requestCode: Int
)