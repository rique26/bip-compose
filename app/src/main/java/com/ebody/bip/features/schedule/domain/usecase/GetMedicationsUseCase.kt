package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.features.schedule.domain.MedicationRepository
import com.ebody.bip.features.schedule.domain.model.Medication
import javax.inject.Inject

class GetMedicationsUseCase @Inject constructor(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(query: String = ""): List<Medication> {
        return repository.getMedications(query)
    }
}