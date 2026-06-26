package com.ebody.bip.features.schedule.data.datasource.local

import com.ebody.bip.features.schedule.data.model.MedicationTakenEntity
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import kotlinx.coroutines.flow.Flow

interface MedicationLocalDataSource {
    suspend fun insertReminder(reminder: MedicationReminder): Long
    fun getActiveReminders(): Flow<List<MedicationReminder>>
    suspend fun deleteReminder(reminder: MedicationReminder)
    suspend fun getMedicationById(id: Long): Medication?
    suspend fun insertReminders(reminders: List<ReminderEntity>)
    suspend fun getRemindersStatusBetween(start: Long, end: Long): List<Boolean>
    suspend fun insertMedicationLog(log: MedicationTakenEntity)
}