package com.ebody.bip.features.schedule.data.repository

import com.ebody.bip.features.schedule.data.local.MedicationDao
import com.ebody.bip.features.schedule.data.mapper.toDomain
import com.ebody.bip.features.schedule.domain.repository.MedicationRepository
import com.ebody.bip.features.schedule.domain.model.Medication
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao
) : MedicationRepository {

    override suspend fun getMedications(query: String): List<Medication> {
        return medicationDao.searchMedications(query).map { it.toDomain() }
    }

    override suspend fun getMedicationById(id: Long): Medication? {
        return medicationDao.getMedicationById(id)?.toDomain()
    }
}