package com.ebody.bip.features.wellbeing.data.model

import com.ebody.bip.core.domain.intelligence.model.RiskLevel

data class MoodRemoteEntity(
    val id: Long = 0L,
    val level: Int = 0,
    val notes: String = "",
    val dateTime: Any = "",
    val riskLevel: String = RiskLevel.ESTAVEL.name,
    val aiInstruction: String = ""
)