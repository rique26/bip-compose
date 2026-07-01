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
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class SaveMoodWithAiAnalysisUseCaseTest {

    private val intelligenceRepository: ClinicalIntelligenceRepository = mockk()
    private val moodRepository: MoodRepository = mockk()
    private lateinit var useCase: SaveMoodWithAiAnalysisUseCase

    @Before
    fun setUp() {
        useCase = SaveMoodWithAiAnalysisUseCase(intelligenceRepository, moodRepository)
    }

    @Test
    fun `quando IA retorna sucesso, deve retornar entrada enriquecida com risco e instrucao`() = runTest {
        val mood = MoodEntry(level = 1, notes = "Tudo ok", dateTime = LocalDateTime.now())
        val analysis = RiskAnalysis(RiskLevel.ESTAVEL, "Continue se cuidando.")

        coEvery { intelligenceRepository.analyzeSymptomRisk(any()) } returns Result.Success(analysis)
        coEvery { moodRepository.saveMood(any()) } answers { firstArg() }

        val result = useCase(mood)

        assertEquals(RiskLevel.ESTAVEL, result.riskLevel)
        assertEquals("Continue se cuidando.", result.aiInstruction)
        coVerify(exactly = 1) { moodRepository.saveMood(any()) }
    }

    @Test
    fun `quando IA falha, deve aplicar fallback de seguranca e salvar registro`() = runTest {
        val mood = MoodEntry(level = 3, notes = "Dores intensas", dateTime = LocalDateTime.now())

        coEvery { intelligenceRepository.analyzeSymptomRisk(any()) } returns Result.Error(Exception("Falha na GPU"))
        coEvery { moodRepository.saveMood(any()) } answers { firstArg() }

        val result = useCase(mood)

        assertEquals(RiskLevel.ESTAVEL, result.riskLevel)
        // Como não tem log, basta validar o estado do objeto
        assertTrue(result.aiInstruction.contains("consulte seu médico"))
        coVerify(exactly = 1) { moodRepository.saveMood(any()) }
    }
}