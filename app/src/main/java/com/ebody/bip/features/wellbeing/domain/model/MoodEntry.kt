package com.ebody.bip.features.wellbeing.domain.model

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import java.time.LocalDateTime

data class MoodEntry(
    val id: Long = 0L,
    val level: Int?,
    val notes: String,
    val dateTime: LocalDateTime,
    val riskLevel: RiskLevel = RiskLevel.ESTAVEL,
    val aiInstruction: String = ""
)