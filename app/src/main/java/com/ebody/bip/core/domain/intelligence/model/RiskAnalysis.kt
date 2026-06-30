package com.ebody.bip.core.domain.intelligence.model

data class RiskAnalysis(
    val riskLevel: RiskLevel,
    val instruction: String
)

enum class RiskLevel {
    ESTAVEL, ALERTA, CRITICO
}