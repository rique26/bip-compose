package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.core.domain.intelligence.repository.MedicationIntelligenceRepository
import javax.inject.Inject

class MarkMedicationTakenUseCase @Inject constructor(
    private val repository: MedicationIntelligenceRepository
) {
    suspend operator fun invoke(reminderId: Long) {
        repository.markMedicationAsTaken(reminderId)
    }
}