package com.ebody.bip.features.schedule.domain.usecase

import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.repository.MedicationRepository
import javax.inject.Inject

class GetMedicationByIdUseCase @Inject constructor(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(id: Long): Medication? {
        return repository.getMedicationById(id)
    }
}
