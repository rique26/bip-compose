package com.ebody.bip.features.schedule.presentation.medication_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class Medication(
    val id: String = "",
    val name: String = "",
    val dosage: String = "", // e.g., "5 mg"
    val frequency: String = "", // e.g., "2 Comprimidos"
    val description: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)


sealed class MedicationListUiState {
    object Loading : MedicationListUiState()
    object Success : MedicationListUiState()
    data class Error(val message: String) : MedicationListUiState()
}

@HiltViewModel
class MedicationListViewModel @Inject constructor(
    // Inject your repository here when ready
    // private val medicationRepository: MedicationRepository
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MedicationListUiState>(MedicationListUiState.Loading)
    val uiState: StateFlow<MedicationListUiState> = _uiState.asStateFlow()

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    // Função de teste rápido para Logout
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            // Como o AuthSharedViewModel está observando o DataStore na MainActivity,
            // a navegação para o AuthGraph deve acontecer automaticamente.
        }
    }
    fun loadMedications() {
        viewModelScope.launch {
            try {
                _uiState.value = MedicationListUiState.Loading
                // TODO: Replace with actual repository call
                // val result = medicationRepository.getMedications()
                // _medications.value = result

                // Mock data for now
                _medications.value = listOf(
                    Medication(
                        id = "1",
                        name = "ACEDERA D3",
                        dosage = "",
                        frequency = "",
                        description = ""
                    ),
                    Medication(
                        id = "2",
                        name = "STABIL",
                        dosage = "",
                        frequency = "",
                        description = ""
                    ),
                    Medication(
                        id = "3",
                        name = "FRED-FORT",
                        dosage = "",
                        frequency = "",
                        description = ""
                    ),
                    Medication(
                        id = "4",
                        name = "RAZAPINA",
                        dosage = "",
                        frequency = "",
                        description = ""
                    )
                )
                _uiState.value = MedicationListUiState.Success
            } catch (e: Exception) {
                _uiState.value = MedicationListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            try {
                // TODO: Replace with actual repository call
                // medicationRepository.deleteMedication(medicationId)
                _medications.value = _medications.value.filter { it.id != medicationId }
            } catch (e: Exception) {
                _uiState.value = MedicationListUiState.Error(e.message ?: "Error deleting medication")
            }
        }
    }


}