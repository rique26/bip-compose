package com.ebody.bip.features.emergency.presentation.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.emergency.domain.usecase.TriggerEmergencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val triggerEmergencyUseCase: TriggerEmergencyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmergencyUiState>(EmergencyUiState.Initial)
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    fun onEmergencyTriggered() {
        viewModelScope.launch {
            _uiState.value = EmergencyUiState.Loading
            try {
                triggerEmergencyUseCase()
                _uiState.value = EmergencyUiState.Success
            } catch (e: Exception) {
                _uiState.value = EmergencyUiState.Error(e.localizedMessage ?: "Erro ao enviar sinal de emergência")
            }
        }
    }

    fun resetState() {
        _uiState.value = EmergencyUiState.Initial
    }
}