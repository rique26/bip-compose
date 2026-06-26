package com.ebody.bip.features.emergency.data.datasource.local

import com.ebody.bip.features.emergency.data.local.ContactDao
import com.ebody.bip.features.emergency.data.local.ContactEntity
import com.ebody.bip.features.emergency.data.mapper.toDomain
import com.ebody.bip.features.emergency.data.mapper.toEntity
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmergencyLocalDataSourceImpl @Inject constructor(
    private val contactDao: ContactDao
) : EmergencyLocalDataSource {

    override suspend fun insertContact(contact: EmergencyContact): Long {
        return contactDao.insertContact(contact.toEntity())
    }

    override fun getAllContacts(): Flow<List<EmergencyContact>> {
        return contactDao.getAllContacts().map { contactEntities ->
            contactEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun deleteContact(contact: EmergencyContact) {
        contactDao.deleteContact(contact.toEntity())
    }

    override suspend fun getContactById(id: Long): EmergencyContact? {
        return contactDao.getContactById(id)?.toDomain()
    }

    override suspend fun insertContacts(contacts: List<ContactEntity>) {
        contactDao.insertContacts(contacts)
    }
}