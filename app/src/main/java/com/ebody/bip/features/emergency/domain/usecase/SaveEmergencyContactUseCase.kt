package com.ebody.bip.features.emergency.domain.usecase

import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import javax.inject.Inject

class SaveEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyRepository
) {
    suspend operator fun invoke(contact: EmergencyContact) {
        repository.saveContact(contact)
    }
}