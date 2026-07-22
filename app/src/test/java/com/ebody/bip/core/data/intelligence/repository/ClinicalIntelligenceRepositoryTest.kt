package com.ebody.bip.core.data.intelligence.repository

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.intelligence.repository.LlmInferenceEngine
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class ClinicalIntelligenceRepositoryTest {

    private val llmEngine: LlmInferenceEngine = mockk()
    private lateinit var repository: ClinicalIntelligenceRepositoryImpl

    @Before
    fun setUp() {
        repository = ClinicalIntelligenceRepositoryImpl(llmEngine)
    }

    @Test
    fun `deve retornar Success com o risco correto quando a IA responder um JSON valido`() = runTest {
        // Uma entrada de humor e uma resposta JSON simulando a IA
        val moodEntry = MoodEntry(level = 1, notes = "Crise de ansiedade forte", dateTime = LocalDateTime.now())

        val jsonResponseIa = """
            {
                "riskLevel": "CRITICO",
                "instruction": "Procure ajuda especializada imediatamente."
            }
        """.trimIndent()

        // Ensaia o motor do Gemma para devolver a String JSON sem processar nada real
        coEvery { llmEngine.generateResponse(any(), any()) } returns jsonResponseIa

        // Executa a análise
        val result = repository.analyzeSymptomRisk(moodEntry)

        // Valida se o repositório parseou o JSON perfeitamente para os Enums de Domínio
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data

        assertEquals(RiskLevel.CRITICO, data.riskLevel)
        assertEquals("Procure ajuda especializada imediatamente.", data.instruction)
    }
}