package com.ebody.bip.core.data.intelligence

import kotlinx.serialization.Serializable

@Serializable
data class AiAnalysisDto(
    val riskLevel: String,
    val instruction: String
)