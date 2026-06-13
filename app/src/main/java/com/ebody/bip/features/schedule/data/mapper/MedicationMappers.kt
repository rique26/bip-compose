package com.ebody.bip.features.schedule.data.mapper

import com.ebody.bip.features.schedule.data.local.MedicationEntity
import com.ebody.bip.features.schedule.domain.model.Medication

fun MedicationEntity.toDomain(): Medication {
    return Medication(
        id = this.id,
        name = this.name
    )
}

fun Medication.toEntity(): MedicationEntity {
    return MedicationEntity(
        id = this.id,
        name = this.name
    )
}