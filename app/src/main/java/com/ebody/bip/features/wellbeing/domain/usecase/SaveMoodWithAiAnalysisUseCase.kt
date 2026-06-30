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

    suspend operator fun invoke(moodEntry: MoodEntry): MoodEntry {
        Log.d(TAG, "Processando análise de risco via IA...")

        // 1. Delega para a camada de infraestrutura a execução da IA
        val analysisResult = intelligenceRepository.analyzeSymptomRisk(moodEntry)

        // 2. Regra de Negócio/Orquestração: Enriquecimento do Modelo
        var enrichedEntry = moodEntry

        analysisResult.onSuccess { analysis ->
            Log.d(TAG, "Inferência concluída. Risco: ${analysis.riskLevel}")
            enrichedEntry = enrichedEntry.copy(
                riskLevel = analysis.riskLevel,
                aiInstruction = analysis.instruction
            )
        }

        // Fallback elegante caso o Gemma estoure memória ou falhe localmente
        if (analysisResult is Result.Error) {
            Log.w(TAG, "Falha na IA. Aplicando fallback de segurança.", analysisResult.error)
            enrichedEntry = enrichedEntry.copy(
                riskLevel = RiskLevel.ESTAVEL,
                aiInstruction = "Registro salvo com sucesso. Em caso de dúvidas, consulte seu médico."
            )
        }

        // Retorna o objeto completo que foi salvo (com ID do Room + dados da IA)
        return moodRepository.saveMood(enrichedEntry)
    }
}