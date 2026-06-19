package com.ebody.bip.features.schedule.domain.repository

import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun saveReminder(reminder: MedicationReminder)
    suspend fun deleteReminder(reminder: MedicationReminder)
    fun getReminders(): Flow<List<MedicationReminder>>

}

