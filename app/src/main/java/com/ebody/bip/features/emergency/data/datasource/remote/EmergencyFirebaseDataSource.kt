package com.ebody.bip.features.emergency.data.datasource.remote

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.domain.model.EmergencyError

interface EmergencyFirebaseDataSource {
    suspend fun syncContact(userId: String, contact: EmergencyContact): Result<Unit, EmergencyError>
    suspend fun deleteContact(userId: String, contactId: Long): Result<Unit, EmergencyError>
    suspend fun fetchAllContacts(userId: String): Result<List<EmergencyContact>, EmergencyError>
}