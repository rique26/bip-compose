package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.intelligence.repository.ClinicalIntelligenceRepository
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.core.domain.util.onSuccess
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

            var enrichedEntry = moodEntry

            if (analysisResult is Result.Error) {
                enrichedEntry = enrichedEntry.copy(
                    riskLevel = RiskLevel.ESTAVEL,
                    aiInstruction = "Humor registrado com sucesso."
                )
            } else {
                analysisResult.onSuccess { analysis ->
                    enrichedEntry = enrichedEntry.copy(
                        riskLevel = analysis.riskLevel,
                        aiInstruction = analysis.instruction
                    )
                }
            }
            val savedEntry = moodRepository.saveMood(enrichedEntry)
            Result.Success(savedEntry)

        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}