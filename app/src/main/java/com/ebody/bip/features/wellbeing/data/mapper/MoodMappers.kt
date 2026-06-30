package com.ebody.bip.features.wellbeing.data.mapper

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import com.ebody.bip.features.wellbeing.data.model.MoodRemoteEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun MoodEntity.toDomain(): MoodEntry {
    return MoodEntry(
        id = this.id,
        level = this.level,
        notes = this.notes,
        dateTime = LocalDateTime.parse(timestamp, formatter),
        riskLevel = runCatching { RiskLevel.valueOf(riskLevel) }.getOrDefault(RiskLevel.ESTAVEL),
        aiInstruction = aiInstruction
    )
}

fun MoodEntry.toEntity(): MoodEntity {
    return MoodEntity(
        id = this.id,
        level = this.level,
        notes = this.notes,
        timestamp = this.dateTime.toString(),
        riskLevel = riskLevel.name,
        aiInstruction = aiInstruction
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