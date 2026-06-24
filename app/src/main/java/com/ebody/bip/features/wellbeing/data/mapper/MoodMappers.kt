package com.ebody.bip.features.wellbeing.data.mapper

import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import java.time.LocalDateTime

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