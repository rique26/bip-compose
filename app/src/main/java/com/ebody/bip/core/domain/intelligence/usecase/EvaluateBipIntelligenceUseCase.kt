package com.ebody.bip.core.domain.intelligence.usecase

import com.ebody.bip.core.domain.intelligence.heuristics.BipHeuristic
import com.ebody.bip.core.domain.intelligence.model.BipAnalysisResult
import com.ebody.bip.core.domain.intelligence.model.BipMascotExpression
import com.ebody.bip.core.domain.intelligence.model.BipRiskLevel
import javax.inject.Inject

class EvaluateBipIntelligenceUseCase @Inject constructor(
    private val heuristics: Set<@JvmSuppressWildcards BipHeuristic>
) {
    suspend operator fun invoke(): BipAnalysisResult {
        // Executa todas as heurísticas e filtra as que retornaram um risco detectado
        val detectedRisks = heuristics.mapNotNull { it.evaluate() }

        // Define o nível de risco como o mais grave (maior ordinal no enum) ou Estável por padrão
        val highestRisk = detectedRisks.maxOrNull() ?: BipRiskLevel.STABLE

        return when (highestRisk) {
            BipRiskLevel.STABLE -> BipAnalysisResult(
                riskLevel = BipRiskLevel.STABLE,
                message = "Continue mantendo sua rotina em dia! Estou de olho para te ajudar.",
                mascotExpression = BipMascotExpression.HAPPY
            )
            BipRiskLevel.ALERT -> BipAnalysisResult(
                riskLevel = BipRiskLevel.ALERT,
                message = "Notei algumas variações na sua rotina ou sintomas. Beba água, descanse e cuide-se.",
                mascotExpression = BipMascotExpression.CONCERNED
            )
            BipRiskLevel.HIGH_RISK -> BipAnalysisResult(
                riskLevel = BipRiskLevel.HIGH_RISK,
                message = "Identifiquei um padrão de alerta prolongado ou crítico. Considere buscar ajuda médica imediatamente.",
                mascotExpression = BipMascotExpression.WORRIED
            )
        }
    }
}