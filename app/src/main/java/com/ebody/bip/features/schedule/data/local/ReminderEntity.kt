package com.ebody.bip.features.schedule.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    val time: Long,
    val dosage: String,
    val createdAt: Long,
    val requestCode: Int,
    val isActive: Boolean = true
)