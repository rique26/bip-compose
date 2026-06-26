package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import javax.inject.Inject

class SyncRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke() = repository.syncWithRemote()
}