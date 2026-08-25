package com.ebody.bip.features.schedule.presentation.medication_schedule

import app.cash.turbine.test
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationByIdUseCase
import com.ebody.bip.features.schedule.domain.usecase.SaveReminderUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MedicationScheduleViewModelTest {

    private val getMedicationByIdUseCase: GetMedicationByIdUseCase = mockk()
    private val saveReminderUseCase: SaveReminderUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MedicationScheduleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MedicationScheduleViewModel(
            getMedicationByIdUseCase = getMedicationByIdUseCase,
            saveReminderUseCase = saveReminderUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMedications updates state successfully`() = runTest {
        // Arrange
        val medicationId = 1L
        val mockMedication = Medication(id = medicationId, name = "Paracetamol")

        coEvery { getMedicationByIdUseCase(medicationId) } returns mockMedication

        // Act & Assert
        viewModel.uiState.test {
            // Estado inicial
            val initialState = awaitItem()
            assertTrue(initialState.medications.isEmpty())

            // Dispara o evento
            viewModel.onEvent(MedicationScheduleEvent.LoadMedications(listOf(medicationId)))

            // Aguarda a atualização do estado
            val updatedState = awaitItem()
            assertEquals(1, updatedState.medications.size)
            assertEquals("Paracetamol", updatedState.medications[0].name)
        }
    }

    @Test
    fun `updateDosage sets dosage and closes dialog`() = runTest {
        // Arrange
        val newDosage = "1 comprimido"

        // Act & Assert
        viewModel.uiState.test {
            skipItems(1) // Ignora estado inicial

            viewModel.onEvent(MedicationScheduleEvent.UpdateDosage(newDosage))

            val state = awaitItem()
            assertEquals(newDosage, state.dosage)
            assertFalse(state.showDosageDialog)
        }
    }

    @Test
    fun `addScheduleTime appends new time when under limit`() = runTest {
        // Arrange & Act & Assert
        viewModel.uiState.test {
            val initialState = awaitItem()
            val initialSize = initialState.scheduleTimes.size

            viewModel.onEvent(MedicationScheduleEvent.AddScheduleTime())

            val updatedState = awaitItem()
            assertEquals(initialSize + 1, updatedState.scheduleTimes.size)
        }
    }

    @Test
    fun `removeScheduleTime removes time correctly when above minimum`() = runTest {
        // Arrange
        viewModel.onEvent(MedicationScheduleEvent.AddScheduleTime()) // Fica com 2 horários

        viewModel.uiState.test {
            skipItems(1) // Pula estado anterior

            // Act
            viewModel.onEvent(MedicationScheduleEvent.RemoveScheduleTime(1))

            // Assert
            val state = awaitItem()
            assertEquals(1, state.scheduleTimes.size)
        }
    }

    @Test
    fun `saveReminders triggers usecase for each medication and time and calls onSuccess`() = runTest {
        // Arrange
        val mockMedication = Medication(id = 1L, name = "Dipirona")
        coEvery { getMedicationByIdUseCase(1L) } returns mockMedication
        coEvery { saveReminderUseCase(any()) } returns Unit

        // Carrega o medicamento e aguarda a corrotina terminar
        viewModel.onEvent(MedicationScheduleEvent.LoadMedications(listOf(1L)))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(MedicationScheduleEvent.UpdateDosage("500mg"))

        var successCalled = false
        val onSuccess = { successCalled = true }

        // Act
        viewModel.onEvent(MedicationScheduleEvent.SaveReminders(onSuccess))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
        coVerify(exactly = 1) { saveReminderUseCase(any()) }
    }

    @Test
    fun `saveReminders with three custom times and 3 pills dosage should trigger usecase 3 times`() = runTest {
        // Arrange
        val medicationId = 1L
        val mockMedication = Medication(id = medicationId, name = "Paracetamol")

        coEvery { getMedicationByIdUseCase(medicationId) } returns mockMedication
        coEvery { saveReminderUseCase(any()) } returns Unit

        // 1. Carrega o medicamento
        viewModel.onEvent(MedicationScheduleEvent.LoadMedications(listOf(medicationId)))
        testDispatcher.scheduler.advanceUntilIdle()

        // 2. Define a dosagem para "3 comprimidos"
        viewModel.onEvent(MedicationScheduleEvent.UpdateDosage("3 comprimidos"))

        // 3. Configura os 3 horários solicitados: 09:00, 09:10 e 09:15
        // Atualiza o primeiro horário (índice 0) para 09:00
        viewModel.onEvent(MedicationScheduleEvent.OpenTimePicker(0, Pair(8, 0)))
        viewModel.onEvent(MedicationScheduleEvent.ConfirmTimePicker(9, 0))

        // Adiciona e configura o segundo horário (índice 1) para 09:10
        viewModel.onEvent(MedicationScheduleEvent.AddScheduleTime())
        viewModel.onEvent(MedicationScheduleEvent.OpenTimePicker(1, Pair(8, 0)))
        viewModel.onEvent(MedicationScheduleEvent.ConfirmTimePicker(9, 10))

        // Adiciona e configura o terceiro horário (índice 2) para 09:15
        viewModel.onEvent(MedicationScheduleEvent.AddScheduleTime())
        viewModel.onEvent(MedicationScheduleEvent.OpenTimePicker(2, Pair(8, 0)))
        viewModel.onEvent(MedicationScheduleEvent.ConfirmTimePicker(9, 15))

        var successCalled = false
        val onSuccess = { successCalled = true }

        // Act: Dispara o salvamento
        viewModel.onEvent(MedicationScheduleEvent.SaveReminders(onSuccess))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: Valida que a operação foi bem-sucedida e que o use case salvou exatamente 3 lembretes (um para cada horário)
        assertTrue(successCalled)
        coVerify(exactly = 3) { saveReminderUseCase(any()) }
    }
}