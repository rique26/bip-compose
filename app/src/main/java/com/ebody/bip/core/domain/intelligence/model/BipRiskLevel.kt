package com.ebody.bip.core.domain.intelligence.model

enum class BipRiskLevel {
    STABLE, ALERT, HIGH_RISK
}

enum class BipMascotExpression {
    HAPPY, CONCERNED, WORRIED
}

data class BipAnalysisResult(
    val riskLevel: BipRiskLevel,
    val message: String,
    val mascotExpression: BipMascotExpression,
    val timestamp: Long = System.currentTimeMillis()
)