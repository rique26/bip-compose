package com.ebody.bip.features.schedule.data.repository

import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.local.ReminderDao
import com.ebody.bip.features.schedule.data.mapper.toDomain
import com.ebody.bip.features.schedule.data.mapper.toEntity
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val medicationDao: MedicationDao
) : ReminderRepository {

    override suspend fun saveReminder(reminder: MedicationReminder) {
        val entity = reminder.toEntity()
        reminderDao.insertReminder(entity)
    }

    override fun getReminders(): Flow<List<MedicationReminder>> {
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
        // Presumindo que seu ReminderEntity tenha uma relação direta com o ID
        reminderDao.deleteReminder(reminder.toEntity())
    }

//    override suspend fun saveMedicationReminder(medication: Medication, reminder: MedicationReminder) {
//        // 1. Salva o medicamento no catálogo local (ignora se já existir)
//        medicationDao.insertMedication(medication.toEntity())
//
//        // 2. Salva o alarme atrelado ao ID desse medicamento
//        medicationDao.insertReminder(reminder.toEntity())
//    }



}
