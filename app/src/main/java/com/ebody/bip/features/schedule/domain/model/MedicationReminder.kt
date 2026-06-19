package com.ebody.bip.features.schedule.domain.model

import java.time.LocalTime

data class MedicationReminder(
    val id: Long = 0,
    val medication: Medication,
    val dosage: String,
    val time: LocalTime,
    val createdAt: Long,
    val requestCode: Int
)