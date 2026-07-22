package com.ebody.bip.features.wellbeing.domain.usecase

import android.util.Log
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.core.domain.util.onSuccess
import com.ebody.bip.features.intelligence.domain.repository.ClinicalIntelligenceRepository
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import javax.inject.Inject

class SaveMoodWithAiAnalysisUseCase @Inject constructor(
    private val intelligenceRepository: ClinicalIntelligenceRepository,
    private val moodRepository: MoodRepository
) {
    companion object {
        private const val TAG = "SaveMoodWithAiUseCase"
    }

    suspend operator fun invoke(moodEntry: MoodEntry): Result<MoodEntry, Exception> {
        if (moodEntry.level == null) {
            return Result.Error(IllegalArgumentException("Selecione um nível de humor"))
        }

        return try {
            val analysisResult = intelligenceRepository.analyzeSymptomRisk(moodEntry)

            if (analysisResult is Result.Error) {
                Log.w(TAG, "Falha na análise de IA. Operação abortada para proteger o histórico.")
                return Result.Error(analysisResult.error)
            }

            var enrichedEntry = moodEntry

            // Usando sua própria DSL funcional de forma limpa
            analysisResult.onSuccess { analysis ->
                enrichedEntry = enrichedEntry.copy(
                    riskLevel = analysis.riskLevel,
                    aiInstruction = analysis.instruction
                )
            }

            val savedEntry = moodRepository.saveMood(enrichedEntry)
            Result.Success(savedEntry)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}