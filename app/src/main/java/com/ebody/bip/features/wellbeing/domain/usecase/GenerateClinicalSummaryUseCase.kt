package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.core.domain.intelligence.repository.ClinicalIntelligenceRepository
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.model.TimeFilter
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GenerateClinicalSummaryUseCase @Inject constructor(
    private val intelligenceRepository: ClinicalIntelligenceRepository
) {
    suspend operator fun invoke(records: List<MoodEntry>, filterLabel: String): String {
        if (records.isEmpty()) return "Nenhum histórico textual disponível para o período selecionado."

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val structuredHistory = records.joinToString("\n") { entry ->
            "- [${entry.dateTime.format(formatter)}] Humor/Nível: ${entry.level}. Notas: ${entry.notes.ifBlank { "Sem observações textuais." }}"
        }

        return intelligenceRepository.generateClinicalSummary(
            structuredHistory = structuredHistory,
            filterLabel = filterLabel
        )
    }
}