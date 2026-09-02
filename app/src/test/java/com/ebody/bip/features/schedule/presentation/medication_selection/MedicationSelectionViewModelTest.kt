package com.ebody.bip.features.schedule.presentation.medication_selection

import android.util.Log
import app.cash.turbine.test
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MedicationSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getMedicationsUseCase: GetMedicationsUseCase = mockk()

    private lateinit var viewModel: MedicationSelectionViewModel

    @Before
    fun setUp() {
        // Configura o Dispatcher principal para testes com corrotinas
        Dispatchers.setMain(testDispatcher)

        // Mocka o Log do Android para evitar RuntimeException em testes unitários puros
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMedications updates uiState to Success and medications when useCase returns data successfully`() = runTest {
        // Arrange
        val mockMedications = listOf(
            Medication(id = 1, name = "Paracetamol"),
            Medication(id = 2, name = "Ibuprofeno")
        )

        coEvery { getMedicationsUseCase("") } returns mockMedications

        // Act
        viewModel = MedicationSelectionViewModel(getMedicationsUseCase)
        testScheduler.advanceUntilIdle()// Processa as corrotinas pendentes no init

        // Assert
        val currentState = viewModel.uiState.value
        assertEquals(mockMedications, currentState.medications)
        assertEquals(false, currentState.isLoading)
        assertEquals(null, currentState.errorMessage)
    }


    @Test
    fun `loadMedications updates uiState with error when useCase throws an exception`() = runTest {
        // Arrange
        val errorMessage = "Connection failed"
        coEvery { getMedicationsUseCase("") } throws RuntimeException(errorMessage)

        // Act
        viewModel = MedicationSelectionViewModel(getMedicationsUseCase)
        testScheduler.advanceUntilIdle()

        // Assert
        val currentState = viewModel.uiState.value
        assertEquals(emptyList<Medication>(), currentState.medications)
        assertEquals(errorMessage, currentState.errorMessage)
        assertEquals(false, currentState.isLoading)
    }

    @Test
    fun `onSearchQueryChanged updates searchQuery in state and triggers medication loading`() = runTest {
        // Arrange
        val query = "Para"
        val mockMedications = listOf(Medication(id = 1L, name = "Paracetamol"))

        coEvery { getMedicationsUseCase("") } returns emptyList()
        coEvery { getMedicationsUseCase(query) } returns mockMedications

        viewModel = MedicationSelectionViewModel(getMedicationsUseCase)
        testScheduler.advanceUntilIdle()

        // Act & Assert usando Turbine para monitorar as emissões do uiState
        viewModel.uiState.test {
            // Descarta o estado atual carregado no init
            assertEquals("", awaitItem().searchQuery)

            // Dispara a mudança de busca via evento
            viewModel.onEvent(MedicationSelectionEvent.SearchQueryChanged(query))

            // Como loadMedications altera o estado para loading=true e depois para sucesso,
            // podemos avançar o tempo e garantir que chegamos no estado final desejado
            testScheduler.advanceUntilIdle()

            // Consome as emissões até encontrar o estado com os medicamentos carregados
            var currentState = awaitItem()
            while (currentState.isLoading || currentState.medications != mockMedications) {
                currentState = awaitItem()
            }

            // Valida se o estado final contém a nova query e os medicamentos corretos
            assertEquals(query, currentState.searchQuery)
            assertEquals(mockMedications, currentState.medications)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when user selects multiple medications, they are correctly stored in state ready for next step`() = runTest {
        // Arrange: Prepara 5 medicamentos simulados
        val fakeMedications = listOf(
            Medication(id = 1L, name = "Paracetamol"),
            Medication(id = 2L, name = "Ibuprofeno"),
            Medication(id = 3L, name = "Dipirona"),
            Medication(id = 4L, name = "Amoxicilina"),
            Medication(id = 5L, name = "Omeprazol")
        )

        coEvery { getMedicationsUseCase("") } returns fakeMedications

        // Act
        viewModel = MedicationSelectionViewModel(getMedicationsUseCase)
        testScheduler.advanceUntilIdle()

        // Simula o usuário selecionando os 5 medicamentos via eventos MVI
        fakeMedications.forEach { medication ->
            viewModel.onEvent(
                MedicationSelectionEvent.ToggleMedicationSelection(
                    id = medication.id,
                    isSelected = true
                )
            )
        }

        // Assert
        val currentState = viewModel.uiState.value

        // 1. Valida se os 5 IDs estão salvos no set
        assertEquals(5, currentState.selectedIds.size)
        assertTrue(currentState.selectedIds.containsAll(listOf(1L, 2L, 3L, 4L, 5L)))

        // 2. Valida se é possível extrair os objetos completos para enviar ao fluxo seguinte
        val selectedMedicationsToSchedule = currentState.medications.filter {
            currentState.selectedIds.contains(it.id)
        }

        assertEquals(5, selectedMedicationsToSchedule.size)
        assertEquals("Paracetamol", selectedMedicationsToSchedule.first().name)
        assertEquals("Omeprazol", selectedMedicationsToSchedule.last().name)
    }
}