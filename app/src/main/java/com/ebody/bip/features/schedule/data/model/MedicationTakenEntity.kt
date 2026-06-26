package com.ebody.bip.features.schedule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_history")
data class MedicationTakenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val reminderId: Long,
    val timestamp: Long,
    val isTaken: Boolean // true = tomado, false = ignorado/perdido
)