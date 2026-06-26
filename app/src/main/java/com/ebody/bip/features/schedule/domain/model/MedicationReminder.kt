package com.ebody.bip.features.schedule.domain.model

import java.time.LocalTime

data class MedicationReminder(
    val id: Long = 0,
    val medication: Medication = Medication(),
    val dosage: String,
    val time: LocalTime = LocalTime.MIDNIGHT,
    val createdAt: Long,
    val requestCode: Int
)