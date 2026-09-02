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

    private lateinit var fakeLlmEngine: FakeLlmInferenceEngineImpl
    private lateinit var repository: ClinicalIntelligenceRepositoryImpl

    @Before
    fun setup() {
        fakeLlmEngine = FakeLlmInferenceEngineImpl()
        repository = ClinicalIntelligenceRepositoryImpl(fakeLlmEngine)
    }

    @Test
    fun analyzeSymptomRisk_whenUserMentionsForgottenMedication_returnsAlertRisk() = runTest {
        // Arrange
        val moodEntry = MoodEntry(level = 2, notes = "Esqueci meu remédio hoje à tarde")

        // Act
        val result = repository.analyzeSymptomRisk(moodEntry)

        // Assert
        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(RiskLevel.ALERTA, data.riskLevel)
        assertTrue(data.instruction.contains("rotina de medicação"))
    }

    @Test
    fun generateClinicalSummary_returnsFormattedMarkdownHeader() = runTest {
        // Act
        val summary = repository.generateClinicalSummary(
            structuredHistory = "Histórico fictício",
            filterLabel = "Últimos 7 dias"
        )

        // Assert
        assertTrue(summary.startsWith("📊 RESUMO CLÍNICO DE ACOMPANHAMENTO"))
        assertTrue(summary.contains("Adesão Medicamentosa:"))
    }
}