package com.ebody.bip.features.emergency.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.emergency.data.mapper.toDomain
import com.ebody.bip.features.emergency.data.mapper.toEntity
import com.ebody.bip.features.emergency.domain.ExternalMessageSender
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmergencyRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val messageSender: ExternalMessageSender,
    @ApplicationContext private val context: Context
) : EmergencyRepository {

    override suspend fun sendEmergencySignal(latitude: Double?, longitude: Double?) {
        val locationLink = if (latitude != null && longitude != null) {
            "Localização aproximada: https://maps.google.com/?q=$latitude,$longitude"
        } else {
            "Não foi possível obter a localização em tempo real."
        }

        val message = "🚨 SOS - ALERTA DE EMERGÊNCIA 🚨\n" +
                "Estou precisando de ajuda!\n$locationLink"

        // Pega a lista atual de contatos salvos no Room
        val activeContacts = contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }.first()

        activeContacts.forEach { contact ->
            if (contact.isWhatsAppEnabled) {
                messageSender.sendWhatsApp(contact.phoneNumber, message)
            }
            if (contact.isSmsEnabled) {
                messageSender.sendSms(contact.phoneNumber, message)
            }
        }
    }

    override fun getContacts(): Flow<List<EmergencyContact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveContact(contact: EmergencyContact) {
        contactDao.insertContact(contact.toEntity())
    }

    override suspend fun deleteContact(contactId: String) {
        val idLong = contactId.toLongOrNull() ?: return
        val contact = contactDao.getContactById(idLong)
        if (contact != null) {
            contactDao.deleteContact(contact)
        }
    }

    override suspend fun toggleWhatsApp(contactId: String) {
        val idLong = contactId.toLongOrNull() ?: return
        val contact = contactDao.getContactById(idLong) ?: return
        val updatedContact = contact.copy(isWhatsAppEnabled = !contact.isWhatsAppEnabled)
        contactDao.insertContact(updatedContact)
    }

    override suspend fun toggleSms(contactId: String) {
        val idLong = contactId.toLongOrNull() ?: return
        val contact = contactDao.getContactById(idLong) ?: return
        val updatedContact = contact.copy(isSmsEnabled = !contact.isSmsEnabled)
        contactDao.insertContact(updatedContact)
    }
}