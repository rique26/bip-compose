package com.ebody.bip.features.schedule.data.datasource.remote

data class MedicationReminderRemote(
    val id: Long = 0L,
    val medicationId: Long = 0L,
    val medicationName: String = "",
    val dosage: String = "",
    val time: String = "00:00",
    val createdAt: Long = 0L,
    val requestCode: Int = 0
)

