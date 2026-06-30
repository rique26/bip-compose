package com.ebody.bip.core.data.intelligence.repository

import android.util.Log
import com.ebody.bip.core.data.intelligence.AiAnalysisDto
import com.ebody.bip.core.domain.intelligence.model.RiskAnalysis
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.intelligence.domain.repository.ClinicalIntelligenceRepository
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ClinicalIntelligenceRepositoryImpl @Inject constructor(
    private val llmEngine: LlmInferenceEngine
) : ClinicalIntelligenceRepository {

    companion object {
        private const val TAG = "IntelligenceRepository"
    }

    override suspend fun analyzeSymptomRisk(moodEntry: MoodEntry): Result<RiskAnalysis, Exception> {
        return try {
            val systemPrompt = """
                Você é o assistente clínico BIP. Analise o nível de humor e anotações. 
                Retorne estritamente um JSON com as chaves exatas: 
                "riskLevel" contendo um dos valores [ESTAVEL, ALERTA, CRITICO] e 
                "instruction" contendo uma orientação curta e amigável.
            """.trimIndent()

            val userPrompt = "Humor Level: ${moodEntry.level}. Notas: ${moodEntry.notes}"
            Log.d(TAG, "Enviando prompt para Gemma no dispositivo. User: $userPrompt")

            // Executa a inferência real no dispositivo
            val rawResponse = llmEngine.generateResponse(systemPrompt, userPrompt)
            Log.d(TAG, "Resposta bruta IA: $rawResponse")

            val dto = Json.decodeFromString<AiAnalysisDto>(rawResponse)
            val risk = runCatching { RiskLevel.valueOf(dto.riskLevel) }.getOrDefault(RiskLevel.ESTAVEL)

            Result.Success(RiskAnalysis(riskLevel = risk, instruction = dto.instruction))
        } catch (e: Exception) {
            Log.e(TAG, "Erro durante inferência da IA no dispositivo.", e)
            Result.Error(e)
        }
    }
}