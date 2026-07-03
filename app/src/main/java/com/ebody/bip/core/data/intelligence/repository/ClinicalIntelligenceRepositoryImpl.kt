package com.ebody.bip.core.data.intelligence.repository

import android.util.Log
import com.ebody.bip.core.data.intelligence.AiAnalysisDto
import com.ebody.bip.core.domain.intelligence.model.RiskAnalysis
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.intelligence.repository.ClinicalIntelligenceRepository
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ClinicalIntelligenceRepositoryImpl @Inject constructor(
    private val llmEngine: LlmInferenceEngine
) : ClinicalIntelligenceRepository {

    companion object {
        private const val TAG = "IntelligenceRepository"
    }

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun analyzeSymptomRisk(moodEntry: MoodEntry): Result<RiskAnalysis, Exception> {
        return try {
            val systemPrompt = """
                VOCÊ É UM ASSISTENTE CLÍNICO DE IA (BIP).
                TAREFA: Analisar o estado emocional e notas do usuário para fornecer um nível de risco e uma orientação de autocuidado.
    
                REGRAS DE SAÍDA:
                1. Responda APENAS com um objeto JSON.
                2. O JSON deve ter estritamente estas duas chaves: "riskLevel" e "instruction".
                3. NÃO inclua textos explicativos, saudações ou blocos de código Markdown.
                4. "riskLevel" deve ser obrigatoriamente: "ESTAVEL", "ALERTA" ou "CRITICO".
    
                EXEMPLO DE RESPOSTA:
                {"riskLevel": "ALERTA", "instruction": "Percebi uma oscilação no seu humor hoje. Tente praticar uma técnica de respiração profunda."}
             """.trimIndent()

            val userPrompt = "Humor Level: ${moodEntry.level}. Notas: ${moodEntry.notes}"

            // Executa a inferência
            val rawResponse = llmEngine.generateResponse(systemPrompt, userPrompt)

            val cleanedResponse = cleanJsonString(rawResponse)
            Log.d(TAG, "JSON limpo para Parse: $cleanedResponse")

            val dto = jsonConfig.decodeFromString<AiAnalysisDto>(cleanedResponse)
            Log.d(TAG, "IA Processada -> Risk: ${dto.riskLevel} | Instruction: ${dto.instruction}")

            val risk = runCatching { RiskLevel.valueOf(dto.riskLevel.uppercase().trim()) }
                .getOrDefault(RiskLevel.ESTAVEL)

            Result.Success(RiskAnalysis(riskLevel = risk, instruction = dto.instruction))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun generateClinicalSummary(
        structuredHistory: String,
        filterLabel: String
    ): String {
        return try {
            val systemPrompt = """
                Você é um assistente de inteligência clínica especializado em sumarizar históricos de pacientes.
                Sua tarefa é ler uma lista de registros de humor e observações de saúde e criar um "RESUMO CLÍNICO DE ACOMPANHAMENTO".
                
                IMPORTANTE: Ignore qualquer prefixo de JSON como '{"riskLevel":' que possa ter sido injetado no bloco do modelo. Forneça estritamente um texto corrido estruturado por extenso em formato Markdown.
                
                Seja extremamente conciso e adote uma linguagem formal médica estruturada estritamente nestes 3 tópicos:
                1. Adesão Medicamentosa (indique a taxa estimada e relate atrasos/esquecimentos com base nas notas).
                2. Volatilidade de Humor e Sintomas (trajetória de estabilidade ou crises).
                3. Correlação Clínica (Insights conectando de forma clara fatores como privação de sono, rotina acadêmica ou estresse aos episódios de piora).
                
                Não use saudações, introduções vazias ou blocos normais de JSON. Comece direto no título do resumo.
            """.trimIndent()

            val userPrompt = """
                Filtro aplicado: $filterLabel
                Histórico bruto extraído do banco de dados local:
                $structuredHistory
            """.trimIndent()

            Log.i(TAG, "Disparando inferência de resumo clínico local...")
            val rawResponse = llmEngine.generateResponse(systemPrompt, userPrompt)

            cleanSummaryText(rawResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao gerar resumo clínico no repositório", e)
            "Falha ao gerar o resumo executivo: ${e.localizedMessage}"
        }
    }

    private fun cleanJsonString(raw: String): String {
        var result = raw.trim()
        if (result.startsWith("```")) {
            result = result.removeSurrounding("```json", "```")
            result = result.removeSurrounding("```", "```")
        }
        return result.trim()
    }

    private fun cleanSummaryText(raw: String): String {
        return raw.replace(Regex("^[^a-zA-Z📊•]+"), "").trim()
    }
}