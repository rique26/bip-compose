package com.ebody.bip.features.emergency.domain.usecase

import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import javax.inject.Inject

class ToggleWhatsAppUseCase @Inject constructor(private val repository: EmergencyRepository) {
    suspend operator fun invoke(contactId: String) = repository.toggleWhatsApp(contactId)
}