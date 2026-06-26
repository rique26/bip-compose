package com.ebody.bip.features.wellbeing.data.mapper

import com.ebody.bip.features.schedule.data.datasource.remote.MedicationReminderRemote
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import com.ebody.bip.features.wellbeing.data.model.MoodRemoteEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun MoodEntity.toDomain(): MoodEntry {
    return MoodEntry(
        id = this.id,
        level = this.level,
        notes = this.notes,
        dateTime = LocalDateTime.parse(this.timestamp)
    )
}

fun MoodEntry.toEntity(): MoodEntity {
    return MoodEntity(
        id = this.id,
        level = this.level,
        notes = this.notes,
        timestamp = this.dateTime.toString()
    )
}

fun MoodRemoteEntity.toDomain(): MoodEntry {
    val dateString = dateTime as? String ?: LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    return MoodEntry(
        id = id,
        level = level,
        notes = notes,
        dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}

fun MoodEntry.toRemoteEntity(): MoodRemoteEntity {
    return MoodRemoteEntity(
        id = id,
        level = level,
        notes = notes,
        dateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}