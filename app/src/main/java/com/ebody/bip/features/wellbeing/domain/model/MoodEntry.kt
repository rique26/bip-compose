package com.ebody.bip.features.wellbeing.domain.model

import java.time.LocalDateTime

data class MoodEntry(
    val id: Long = 0L,
    val level: Int,
    val notes: String,
    val dateTime: LocalDateTime
)