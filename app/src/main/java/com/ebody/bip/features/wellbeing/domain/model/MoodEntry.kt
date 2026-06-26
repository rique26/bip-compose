package com.ebody.bip.features.wellbeing.domain.model

import java.time.LocalDateTime

data class MoodEntry(
    val id: Long = 0L,
    val level: Int,
    val notes: String,
    val dateTime: LocalDateTime
) {
    // Propriedades semânticas limpas para uso nas heurísticas de IA
    val isMoodBad: Boolean
        get() = level == 1 // Ou a constante correspondente ao nível "Mal"

    val isMoodStrange: Boolean
        get() = level == 2 // Ou a constante correspondente ao nível "Estranho"

    val isMoodGood: Boolean
        get() = level == 3 // Ou a constante correspondente ao nível "Bem"

    val isMoodExcellent: Boolean
        get() = level == 4 // Ou a constante correspondente ao nível "Ótimo"
}