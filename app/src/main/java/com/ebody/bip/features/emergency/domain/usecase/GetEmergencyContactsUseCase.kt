package com.ebody.bip.features.emergency.domain.usecase

import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import javax.inject.Inject

class GetEmergencyContactsUseCase @Inject constructor(
    private val repository: EmergencyRepository
) {
    operator fun invoke() = repository.getContacts()
}