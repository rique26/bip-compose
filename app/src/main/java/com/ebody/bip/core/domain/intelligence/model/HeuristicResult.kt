package com.ebody.bip.core.domain.intelligence.model

data class HeuristicResult(
    val severityScore: Float, // 0.0 (estável) a 1.0 (crítico)
    val reason: String
)