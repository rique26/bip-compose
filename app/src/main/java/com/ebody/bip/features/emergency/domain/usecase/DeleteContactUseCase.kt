package com.ebody.bip.features.emergency.domain.usecase

import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(private val repository: EmergencyRepository) {
    suspend operator fun invoke(contactId: String) = repository.deleteContact(contactId)
}