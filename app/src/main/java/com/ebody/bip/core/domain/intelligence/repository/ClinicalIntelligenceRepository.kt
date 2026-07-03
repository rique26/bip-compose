package com.ebody.bip.core.domain.intelligence.repository

import com.ebody.bip.core.domain.intelligence.model.RiskAnalysis
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry

interface ClinicalIntelligenceRepository {
    suspend fun analyzeSymptomRisk(moodEntry: MoodEntry): Result<RiskAnalysis, Exception>

    suspend fun generateClinicalSummary(structuredHistory: String, filterLabel: String): String
}