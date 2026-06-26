package com.ebody.bip.features.emergency.domain.usecase

import com.ebody.bip.features.emergency.domain.repository.EmergencyRepository
import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import javax.inject.Inject

class SyncContactsUseCase @Inject constructor(
    private val repository: EmergencyRepository
) {
    suspend operator fun invoke() = repository.syncWithRemote()
}