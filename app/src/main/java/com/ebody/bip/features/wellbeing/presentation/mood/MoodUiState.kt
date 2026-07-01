package com.ebody.bip.features.wellbeing.presentation.mood

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import java.time.LocalDateTime

enum class MascotExpression { NORMAL, CONCERNED, CRITICAL }

data class MoodUiState(
    val selectedMood: Int? = null,
    val currentDateTime: LocalDateTime = LocalDateTime.now(),
    val notes: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSavedSuccessfully: Boolean = false,
    val aiInstruction: String = "",
    val mascotExpression: MascotExpression = MascotExpression.NORMAL
)

fun RiskLevel?.toMascotExpression(): MascotExpression {
    return when (this) {
        RiskLevel.ALERTA -> MascotExpression.CONCERNED
        RiskLevel.CRITICO -> MascotExpression.CRITICAL
        else -> MascotExpression.NORMAL
    }
}