package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.features.schedule.domain.MedicationRepository
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import javax.inject.Inject

class SaveReminderUseCase @Inject constructor(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(medication: Medication, reminder: MedicationReminder) {
        if (medication.name.isBlank()) throw IllegalArgumentException("O nome do remédio não pode ser vazio")

//        repository.saveMedicationReminder(medication, reminder)
    }
}