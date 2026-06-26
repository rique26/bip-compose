package com.ebody.bip.features.schedule.data.datasource.remote

import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.model.ScheduleError
import com.ebody.bip.core.domain.util.Result

interface MedicationFirebaseDataSource {
    suspend fun syncReminder(userId: String, reminder: MedicationReminder): Result<Unit, ScheduleError>
    suspend fun deleteReminder(userId: String, reminderId: Long): Result<Unit, ScheduleError>
    suspend fun fetchAllReminders(userId: String): Result<List<MedicationReminder>, ScheduleError>
}