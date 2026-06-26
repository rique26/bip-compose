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
        // Executa todas as heurísticas e soma os scores de severidade
        val results = heuristics.mapNotNull { it.evaluate() }

        // Se nenhuma heurística detectou nada, estamos estáveis
        if (results.isEmpty()) {
            return defaultResult(BipRiskLevel.STABLE, "Continue mantendo sua rotina em dia! Estou de olho para te ajudar.", BipMascotExpression.HAPPY)
        }

        // Pega a severidade acumulada. Pode ultrapassar 1.0 se múltiplos fatores estiverem ruins
        val totalSeverity = results.sumOf { it.severityScore.toDouble() }.toFloat()

        // Sistema de Thresholds (Limiares) Ponderados
        return when {
            totalSeverity >= 1.5f -> BipAnalysisResult(
                riskLevel = BipRiskLevel.HIGH_RISK,
                message = "Identifiquei um acúmulo de fatores de alerta. Considere buscar ajuda médica ou revisar seus cuidados imediatamente.",
                mascotExpression = BipMascotExpression.WORRIED
            )
            totalSeverity >= 0.7f -> BipAnalysisResult(
                riskLevel = BipRiskLevel.ALERT,
                message = "Notei algumas variações na sua rotina, humor ou medicação. Beba água, descanse e cuide-se.",
                mascotExpression = BipMascotExpression.CONCERNED
            )
            else -> BipAnalysisResult(
                riskLevel = BipRiskLevel.STABLE,
                message = "Continue mantendo sua rotina em dia! Estou de olho para te ajudar.",
                mascotExpression = BipMascotExpression.HAPPY
            )
        }
    }

    private fun defaultResult(level: BipRiskLevel, msg: String, expr: BipMascotExpression) =
        BipAnalysisResult(riskLevel = level, message = msg, mascotExpression = expr)
}