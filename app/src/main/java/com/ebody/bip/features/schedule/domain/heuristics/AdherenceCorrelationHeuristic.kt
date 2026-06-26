package com.ebody.bip.features.schedule.domain.heuristics

import com.ebody.bip.core.domain.intelligence.heuristics.BipHeuristic
import com.ebody.bip.core.domain.intelligence.model.HeuristicResult
import com.ebody.bip.core.domain.intelligence.repository.MedicationIntelligenceRepository
import javax.inject.Inject

class AdherenceCorrelationHeuristic @Inject constructor(
    private val medicationRepository: MedicationIntelligenceRepository
) : BipHeuristic {

    override suspend fun evaluate(): HeuristicResult? {
        // Pega a taxa de adesão dos últimos 3 dias (escala de 0.0 a 1.0)
        val adherenceRate = medicationRepository.getAdherenceRateLastDays(days = 3)

        // Se a adesão estiver abaixo de 80% (0.8f)
        if (adherenceRate < 0.8f) {
            val severityPenalty = (1.0f - adherenceRate) // Quanto mais baixa, maior a severidade (ex: adesão 0.5 = severidade 0.5)
            return HeuristicResult(
                severityScore = severityPenalty.coerceAtMost(0.9f),
                reason = "Adesão aos medicamentos abaixo de 80% nos últimos 3 dias."
            )
        }
        return null
    }
}