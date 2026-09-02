package com.ebody.bip.features.schedule.presentation.medication_selection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MedicationSelectionUiState(
    val isLoading: Boolean = false,
    val medications: List<Medication> = emptyList(),
    val searchQuery: String = "",
    val selectedIds: Set<Long> = emptySet(),
    val errorMessage: String? = null
)

sealed interface MedicationSelectionEvent {
    data class SearchQueryChanged(val query: String) : MedicationSelectionEvent
    data class ToggleMedicationSelection(val id: Long, val isSelected: Boolean) : MedicationSelectionEvent
    data class LoadMedications(val query: String = "") : MedicationSelectionEvent
}

@HiltViewModel
class MedicationSelectionViewModel @Inject constructor(
    private val getMedicationsUseCase: GetMedicationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationSelectionUiState())
    val uiState: StateFlow<MedicationSelectionUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
    }

    fun onEvent(event: MedicationSelectionEvent) {
        when (event) {
            is MedicationSelectionEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                loadMedications(event.query)
            }
            is MedicationSelectionEvent.ToggleMedicationSelection -> {
                val currentSelected = _uiState.value.selectedIds
                val newSelected = if (event.isSelected) {
                    currentSelected + event.id
                } else {
                    currentSelected - event.id
                }
                _uiState.update { it.copy(selectedIds = newSelected) }
            }
            is MedicationSelectionEvent.LoadMedications -> {
                loadMedications(event.query)
            }
        }
    }

    private fun loadMedications(query: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = getMedicationsUseCase(query)
                _uiState.update {
                    it.copy(isLoading = false, medications = result)
                }
            } catch (e: Exception) {
                Log.e("MedicationSelection", "Erro ao carregar medicações: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Erro desconhecido")
                }
            }
        }
    }
}