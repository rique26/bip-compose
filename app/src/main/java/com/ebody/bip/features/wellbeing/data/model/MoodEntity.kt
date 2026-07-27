package com.ebody.bip.features.wellbeing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ebody.bip.core.domain.intelligence.model.RiskLevel

@Entity(tableName = "mood_table")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val level: Int?,
    val notes: String,
    val timestamp: String,
    val riskLevel: String = RiskLevel.ESTAVEL.name,
    val aiInstruction: String = ""
)