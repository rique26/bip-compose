package com.ebody.bip.features.schedule.presentation.medication_selection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.schedule.data.local.ReminderEntity
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationsUseCase
import com.ebody.bip.features.schedule.domain.usecase.SaveReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationSelectionViewModel @Inject constructor(
    private val getMedicationsUseCase: GetMedicationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MedicationSelectionUiState>(MedicationSelectionUiState.Loading)
    val uiState: StateFlow<MedicationSelectionUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications.asStateFlow()

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        loadMedications(newQuery)
    }

    fun loadMedications(query: String = "") {
        viewModelScope.launch {
            _uiState.value = MedicationSelectionUiState.Loading
            try {
                val result = getMedicationsUseCase(query)
                _medications.value = result
                _uiState.value = MedicationSelectionUiState.Success
            } catch (e: Exception) {
                Log.e("MedicationSelection", "Erro ao carregar medicações: ${e.message}", e)
                _uiState.value = MedicationSelectionUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

//    fun deleteMedication(medicationId: String) {
//        viewModelScope.launch {
//            try {
//                // TODO: Replace with actual repository call
//                // medicationRepository.deleteMedication(medicationId)
//                _medications.value = _medications.value.filter { it.id != medicationId }
//            } catch (e: Exception) {
//                _uiState.value =
//                    MedicationListUiState.Error(e.message ?: "Error deleting medication")
//            }
//        }
//    }


}
