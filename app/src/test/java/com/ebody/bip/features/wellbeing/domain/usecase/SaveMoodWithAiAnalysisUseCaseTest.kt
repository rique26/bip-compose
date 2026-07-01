package com.ebody.bip.features.wellbeing.domain.usecase

import com.ebody.bip.core.domain.intelligence.model.RiskAnalysis
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.intelligence.domain.repository.ClinicalIntelligenceRepository
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.repository.MoodRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class SaveMoodWithAiAnalysisUseCaseTest {

    private val intelligenceRepository: ClinicalIntelligenceRepository = mockk()
    private val moodRepository: MoodRepository = mockk()
    private val sut = SaveMoodWithAiAnalysisUseCase(intelligenceRepository, moodRepository)

    @Test
    fun `deve enriquecer humor com dados da IA e salvar com sucesso quando os repositorios responderem`() = runTest {
        val inputEntry = MoodEntry(
            id = 0L,
            level = 1,
            notes = "Sentindo tonturas fortes",
            dateTime = LocalDateTime.now()
        )

        // Resposta simulada da IA: Identifica um risco crítico com base na nota acima.
        val aiAnalysisResult = RiskAnalysis(
            riskLevel = RiskLevel.CRITICO,
            instruction = "Alerta: Procure ajuda imediatamente."
        )

        // O resultado final que o repositório deve salvar (o dado inicial + os dados da IA).
        val expectedSavedEntry = inputEntry.copy(
            riskLevel = RiskLevel.CRITICO,
            aiInstruction = "Alerta: Procure ajuda imediatamente."
        )

        // Configura o mock da IA: Quando a IA analisar este sintoma, retorne Sucesso com a análise crítica.
        coEvery { intelligenceRepository.analyzeSymptomRisk(inputEntry) } returns Result.Success(aiAnalysisResult)
        // Configura o mock do banco/repositório: Quando mandar salvar qualquer coisa, retorne o objeto enriquecido.
        coEvery { moodRepository.saveMood(any()) } returns expectedSavedEntry

        // Invoca o UseCase passando o input inicial.
        val result = sut(inputEntry)

        // Garante que o UseCase retornou um status de Sucesso.
        assertTrue(result is Result.Success)

        // Faz o cast seguro do resultado para extrair os dados de sucesso.
        val successData = (result as Result.Success).data

        // Verifica se os dados retornados foram de fato enriquecidos corretamente pela IA.
        assertEquals(RiskLevel.CRITICO, successData.riskLevel)
        assertEquals("Alerta: Procure ajuda imediatamente.", successData.aiInstruction)

        // coVerify certifica que o método da IA foi chamado exatamente 1 vez com o input correto.
        coVerify(exactly = 1) { intelligenceRepository.analyzeSymptomRisk(inputEntry) }
        // coVerify certifica que o método de salvar foi chamado exatamente 1 vez com qualquer parâmetro.
        coVerify(exactly = 1) { moodRepository.saveMood(any()) }
    }
}