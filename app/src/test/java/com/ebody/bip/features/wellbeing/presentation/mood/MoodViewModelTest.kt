package com.ebody.bip.features.wellbeing.presentation.mood

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.usecase.SaveMoodWithAiAnalysisUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val saveMoodWithAiAnalysisUseCase: SaveMoodWithAiAnalysisUseCase = mockk()
    private lateinit var viewModel: MoodViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MoodViewModel(saveMoodWithAiAnalysisUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deve transicionar o estado para sucesso e alterar mascote quando o salvamento for bem sucedido`() = runTest {
        val mockDateTime = LocalDateTime.of(2026, 7, 1, 12, 0)
        val expectedSavedEntry = MoodEntry(
            id = 1L,
            level = 2,
            notes = "Dores de cabeça constantes",
            dateTime = mockDateTime,
            riskLevel = RiskLevel.CRITICO,
            aiInstruction = "Busque atendimento médico imediato."
        )

        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Success(expectedSavedEntry)

        viewModel.onEvent(MoodEvent.SelectMood(2))
        viewModel.onEvent(MoodEvent.UpdateNotes("Dores de cabeça constantes"))
        viewModel.onEvent(MoodEvent.UpdateDateTime(mockDateTime))

        viewModel.onEvent(MoodEvent.SaveMood)

        // Libera a execução travada do StandardTestDispatcher até que o fluxo assíncrono finalize
        testDispatcher.scheduler.advanceUntilIdle()

        // Inspeção do estado final da View: sucesso, limpeza de campos e reação do mascote
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isSaving)
        assertTrue(finalState.isSavedSuccessfully)
        assertEquals("Busque atendimento médico imediato.", finalState.aiInstruction)
        assertEquals(MascotExpression.CRITICAL, finalState.mascotExpression)
        assertEquals(null, finalState.selectedMood)
        assertEquals("", finalState.notes)
    }

    @Test
    fun `deve atualizar uiState com mensagem de erro quando o usecase falhar`() = runTest {
        // Força o UseCase a simular uma quebra de fluxo (Exceção)
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Error(Exception("Falha no banco local"))

        viewModel.onEvent(MoodEvent.SelectMood(1))

        // Executa a ação de salvamento destinada a falhar
        viewModel.onEvent(MoodEvent.SaveMood)

        // Esvazia a fila de tarefas da corotina em execução no escopo do ViewModel
        testDispatcher.scheduler.advanceUntilIdle()

        // Garante que a falha foi devidamente mapeada e exposta na UI como feedback de erro
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isSaving)
        assertFalse(finalState.isSavedSuccessfully)
        assertEquals("Falha no banco local", finalState.errorMessage)
    }
}