package com.ebody.bip.features.schedule.data.mapper

import com.ebody.bip.features.schedule.data.model.MedicationEntity
import com.ebody.bip.features.schedule.data.model.ReminderEntity
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun MedicationEntity.toDomain() = Medication(
    id = id,
    name = name
)

fun Medication.toEntity() = MedicationEntity(
    id = id,
    name = name
)

fun ReminderEntity.toDomain(medication: Medication): MedicationReminder {
    return MedicationReminder(
        id = id,
        medication = medication,
        dosage = dosage,
        time = LocalTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()),
        createdAt = createdAt,
        requestCode = requestCode
    )
}

fun MedicationReminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        medicationId = medication.id,
        dosage = dosage,
        time = time.atDate(LocalDate.now())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
        createdAt = createdAt,
        requestCode = requestCode
    )
}