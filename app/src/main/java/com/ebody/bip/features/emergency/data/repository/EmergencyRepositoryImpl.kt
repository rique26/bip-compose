package com.ebody.bip.features.emergency.data.repository

import android.content.Context
import android.util.Log
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.repository.SessionManager
import com.ebody.bip.features.emergency.data.datasource.local.EmergencyLocalDataSource
import com.ebody.bip.features.emergency.data.datasource.remote.EmergencyFirebaseDataSource
import com.ebody.bip.features.emergency.data.mapper.toEntity
import com.ebody.bip.features.emergency.domain.ExternalMessageSender
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmergencyRepositoryImpl @Inject constructor(
    private val localDataSource: EmergencyLocalDataSource,
    private val remoteDataSource: EmergencyFirebaseDataSource,
    private val sessionManager: SessionManager,
    private val messageSender: ExternalMessageSender,
    @ApplicationContext private val context: Context
) : EmergencyRepository {

    companion object {
        private const val TAG = "ReminderRepository"
    }

    private suspend fun getCurrentUserId(): String? {
        return sessionManager.getUserSession().firstOrNull()?.userId
    }

    override suspend fun sendEmergencySignal(latitude: Double?, longitude: Double?) {
        val locationLink = if (latitude != null && longitude != null) {
            "Localização aproximada: https://maps.google.com/?q=$latitude,$longitude"
        } else {
            "Não foi possível obter a localização em tempo real."
        }

        val message = "🚨 SOS - ALERTA DE EMERGÊNCIA 🚨\n" + "Estou precisando de ajuda!\n$locationLink"

        val activeContacts = localDataSource.getAllContacts().first()

        activeContacts.forEach { contact ->
            if (contact.isWhatsAppEnabled) messageSender.sendWhatsApp(contact.phoneNumber, message)
            if (contact.isSmsEnabled) messageSender.sendSms(contact.phoneNumber, message)
        }
    }

    override fun getContacts(): Flow<List<EmergencyContact>> {
        return localDataSource.getAllContacts()
    }

    override suspend fun saveContact(contact: EmergencyContact) {
        val insertedId = localDataSource.insertContact(contact)
        val savedContact = if (contact.id == 0L && insertedId > 0) {
            contact.copy(id = insertedId)
        } else {
            contact
        }

        syncRemoteChange(savedContact)
    }

    override suspend fun deleteContact(contact: EmergencyContact) {
        localDataSource.deleteContact(contact)
        getCurrentUserId()?.let { userId ->
            remoteDataSource.deleteContact(userId, contact.id)
        }
    }

    override suspend fun toggleWhatsApp(contactId: String) {
        val contact = localDataSource.getContactById(contactId.toLongOrNull() ?: return) ?: return
        val updated = contact.copy(isWhatsAppEnabled = !contact.isWhatsAppEnabled)
        localDataSource.insertContact(updated)
        syncRemoteChange(updated)
    }

    override suspend fun toggleSms(contactId: String) {
        val contact = localDataSource.getContactById(contactId.toLongOrNull() ?: return) ?: return
        val updated = contact.copy(isSmsEnabled = !contact.isSmsEnabled)
        localDataSource.insertContact(updated)
        syncRemoteChange(updated)
    }

    override suspend fun syncWithRemote() {
        getCurrentUserId()?.let { userId ->
            when (val remoteResult = remoteDataSource.fetchAllContacts(userId)) {
                is Result.Success -> {
                    val remoteData = remoteResult.data.map { it.toEntity() }
                    localDataSource.insertContacts(remoteData)
                }
                is Result.Error -> Log.e(TAG, "Erro ao sincronizar do remoto para local.")
            }
        }
    }

    private suspend fun syncRemoteChange(contact: EmergencyContact) {
        getCurrentUserId()?.let { userId ->
            try {
                remoteDataSource.syncContact(userId, contact)
            } catch (e: Exception) {
                Log.e(TAG, "Falha na sincronização remota", e)
            }
        }
    }
}