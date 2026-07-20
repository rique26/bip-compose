package com.ebody.bip.features.emergency.domain.repository

import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

interface EmergencyRepository {
    suspend fun sendEmergencySignal(latitude: Double?, longitude: Double?)
    fun getContacts(): Flow<List<EmergencyContact>>
    suspend fun saveContact(contact: EmergencyContact)
    suspend fun deleteContact(contact: EmergencyContact)
    suspend fun toggleWhatsApp(contactId: String)
    suspend fun toggleSms(contactId: String)
    suspend fun syncWithRemote()
}