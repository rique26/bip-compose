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
import org.junit.Assert.assertNotNull
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
        // Arrange
        val mood = MoodEntry(level = 1, notes = "Tudo ok", dateTime = LocalDateTime.now())
        val analysis = RiskAnalysis(RiskLevel.ESTAVEL, "Continue se cuidando.")

        coEvery { intelligenceRepository.analyzeSymptomRisk(any()) } returns Result.Success(analysis)
        coEvery { moodRepository.saveMood(any()) } answers { firstArg() }

        // Act
        val result = useCase(mood)

        // Assert
        assertTrue("Esperado Result.Success", result is Result.Success)
        val savedMood = (result as Result.Success).data

        assertEquals(RiskLevel.ESTAVEL, savedMood.riskLevel)
        assertEquals("Continue se cuidando.", savedMood.aiInstruction)

        coVerify(exactly = 1) { intelligenceRepository.analyzeSymptomRisk(any()) }
        coVerify(exactly = 1) { moodRepository.saveMood(any()) }
    }

    @Test
    fun `quando IA falha, deve abortar operacao e retornar Result Error sem salvar registro`() = runTest {
        // Arrange
        val mood = MoodEntry(level = 3, notes = "Dores intensas", dateTime = LocalDateTime.now())
        val exception = Exception("Falha na GPU")

        coEvery { intelligenceRepository.analyzeSymptomRisk(any()) } returns Result.Error(exception)

        // Act
        val result = useCase(mood)

        // Assert
        assertTrue("Esperado Result.Error ao falhar a IA", result is Result.Error)
        val errorResult = (result as Result.Error).error

        // Garante apenas que existe uma exceção retornada, sem falhar por variação de String
        assertNotNull("Esperado um erro retornado", errorResult)

        coVerify(exactly = 1) { intelligenceRepository.analyzeSymptomRisk(any()) }
        coVerify(exactly = 0) { moodRepository.saveMood(any()) }
    }
}