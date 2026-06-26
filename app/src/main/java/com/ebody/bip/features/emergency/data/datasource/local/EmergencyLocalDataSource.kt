package com.ebody.bip.features.emergency.data.datasource.local

import com.ebody.bip.features.emergency.data.local.ContactEntity
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

interface EmergencyLocalDataSource {
    suspend fun insertContact(contact: EmergencyContact): Long
    fun getAllContacts(): Flow<List<EmergencyContact>>
    suspend fun deleteContact(contact: EmergencyContact)
    suspend fun getContactById(id: Long): EmergencyContact?
    suspend fun insertContacts(contacts: List<ContactEntity>)
}