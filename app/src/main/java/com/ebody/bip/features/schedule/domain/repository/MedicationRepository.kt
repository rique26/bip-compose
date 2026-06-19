package com.ebody.bip.features.schedule.domain.repository

import com.ebody.bip.features.schedule.domain.model.Medication

interface  MedicationRepository {
    suspend fun getMedications(query: String): List<Medication>
    suspend fun getMedicationById(id: Long): Medication?

}