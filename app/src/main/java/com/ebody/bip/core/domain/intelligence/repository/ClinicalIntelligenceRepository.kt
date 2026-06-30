package com.ebody.bip.features.intelligence.domain.repository

import com.ebody.bip.core.domain.intelligence.model.RiskAnalysis
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry

interface ClinicalIntelligenceRepository {
    suspend fun analyzeSymptomRisk(moodEntry: MoodEntry): Result<RiskAnalysis, Exception>
}