package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.features.schedule.domain.AlarmScheduler
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(reminder: MedicationReminder) {
        alarmScheduler.cancel(reminder)
        repository.deleteReminder(reminder)
    }
}