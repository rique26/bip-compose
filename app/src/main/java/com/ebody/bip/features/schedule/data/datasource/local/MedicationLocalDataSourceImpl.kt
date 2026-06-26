package com.ebody.bip.features.schedule.data.datasource.local

import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.mapper.toDomain
import com.ebody.bip.features.schedule.data.mapper.toEntity
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicationLocalDataSourceImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val medicationDao: MedicationDao
) : MedicationLocalDataSource {

    override suspend fun insertReminder(reminder: MedicationReminder): Long {
        return reminderDao.insertReminder(reminder.toEntity())
    }

    override fun getActiveReminders(): Flow<List<MedicationReminder>> {
        return reminderDao.getAllActiveReminders().map { reminderEntities ->
            reminderEntities.map { reminderEntity ->
                val medEntity = medicationDao.getMedicationById(reminderEntity.medicationId)
                val medicationDomain = medEntity?.toDomain()
                    ?: Medication(id = 0, name = "Medicamento não encontrado")
                reminderEntity.toDomain(medicationDomain)
            }
        }
    }

    override suspend fun deleteReminder(reminder: MedicationReminder) {
        reminderDao.deleteReminder(reminder.toEntity())
    }

    override suspend fun getMedicationById(id: Long): Medication? {
        return medicationDao.getMedicationById(id)?.toDomain()
    }

    override suspend fun insertReminders(reminders: List<ReminderEntity>) {
        reminderDao.insertReminders(reminders)
    }
}