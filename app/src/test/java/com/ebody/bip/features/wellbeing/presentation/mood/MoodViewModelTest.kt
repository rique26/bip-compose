package com.ebody.bip.features.wellbeing.presentation.mood

import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.usecase.SaveMoodWithAiAnalysisUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {

    private val saveMoodWithAiAnalysisUseCase: SaveMoodWithAiAnalysisUseCase = mockk()
    private lateinit var viewModel: MoodViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MoodViewModel(saveMoodWithAiAnalysisUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- TESTES DE EVENTOS DE INTERAÇÃO ---

    @Test
    fun `onEvent SelectMood updates selectedMood in uiState`() {
        viewModel.onEvent(MoodEvent.SelectMood(3))
        assertEquals(3, viewModel.uiState.value.selectedMood)
    }

    @Test
    fun `onEvent UpdateNotes updates notes in uiState`() {
        val notes = "Sentindo dor de cabeça leve"
        viewModel.onEvent(MoodEvent.UpdateNotes(notes))
        assertEquals(notes, viewModel.uiState.value.notes)
    }

    @Test
    fun `onEvent UpdateDateTime updates currentDateTime in uiState`() {
        val now = LocalDateTime.now()
        viewModel.onEvent(MoodEvent.UpdateDateTime(now))
        assertEquals(now, viewModel.uiState.value.currentDateTime)
    }

    // --- TESTES DE SALVAMENTO E MAPEAMENTO DE EXPRESSÃO DA IA ---

    @Test
    fun `onEvent SaveMood when RiskLevel is ESTAVEL sets MascotExpression to NORMAL`() = runTest {
        val entry = MoodEntry(
            level = 2,
            notes = "Tudo bem",
            riskLevel = RiskLevel.ESTAVEL,
            aiInstruction = "Continue assim.",
            dateTime = LocalDateTime.now()
        )
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Success(entry)

        viewModel.onEvent(MoodEvent.SelectMood(2))
        viewModel.onEvent(MoodEvent.SaveMood)

        val state = viewModel.uiState.value
        assertTrue(state.isSavedSuccessfully)
        assertEquals("Continue assim.", state.aiInstruction)
        assertEquals(MascotExpression.NORMAL, state.mascotExpression)
    }

    @Test
    fun `onEvent SaveMood when RiskLevel is ALERTA sets MascotExpression to CONCERNED`() = runTest {
        val entry = MoodEntry(
            level = 3,
            notes = "Sinto tontura",
            riskLevel = RiskLevel.ALERTA,
            aiInstruction = "Sente-se e tome água.",
            dateTime = LocalDateTime.now()
        )
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Success(entry)

        viewModel.onEvent(MoodEvent.SelectMood(3))
        viewModel.onEvent(MoodEvent.SaveMood)

        val state = viewModel.uiState.value
        assertTrue(state.isSavedSuccessfully)
        assertEquals(MascotExpression.CONCERNED, state.mascotExpression)
    }

    @Test
    fun `onEvent SaveMood when RiskLevel is CRITICO sets MascotExpression to CRITICAL`() = runTest {
        val entry = MoodEntry(
            level = 4,
            notes = "Dor forte no peito",
            riskLevel = RiskLevel.CRITICO,
            aiInstruction = "Procure atendimento de emergência.",
            dateTime = LocalDateTime.now()
        )
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Success(entry)

        viewModel.onEvent(MoodEvent.SelectMood(4))
        viewModel.onEvent(MoodEvent.SaveMood)

        val state = viewModel.uiState.value
        assertTrue(state.isSavedSuccessfully)
        assertEquals(MascotExpression.CRITICAL, state.mascotExpression)
    }

    @Test
    fun `onEvent SaveMood when useCase fails updates state with errorMessage`() = runTest {
        val errorMessage = "Selecione um nível de humor"
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Error(
            IllegalArgumentException(errorMessage)
        )

        viewModel.onEvent(MoodEvent.SaveMood)

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertFalse(state.isSavedSuccessfully)
        assertEquals(errorMessage, state.errorMessage)
    }

    // --- TESTE DE RESET DE ESTADO ---

    @Test
    fun `onEvent ResetSinks resets temporary feedback properties in uiState`() {
        val entry = MoodEntry(level = 1, notes = "", riskLevel = RiskLevel.ESTAVEL, aiInstruction = "OK", dateTime = LocalDateTime.now())
        coEvery { saveMoodWithAiAnalysisUseCase(any()) } returns Result.Success(entry)
        viewModel.onEvent(MoodEvent.SaveMood)

        viewModel.onEvent(MoodEvent.ResetSinks)

        val state = viewModel.uiState.value
        assertFalse(state.isSavedSuccessfully)
        assertNull(state.errorMessage)
        assertEquals("", state.aiInstruction)
        assertEquals(MascotExpression.NORMAL, state.mascotExpression)
    }
}