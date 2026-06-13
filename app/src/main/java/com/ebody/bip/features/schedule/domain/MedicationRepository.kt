package com.ebody.bip.features.schedule.domain

import com.ebody.bip.core.domain.model.AuthUser
import com.ebody.bip.core.domain.model.BipAuthException
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.schedule.domain.model.Medication
import kotlinx.coroutines.flow.Flow

interface  MedicationRepository {
    suspend fun getMedications(query: String): List<Medication>

}