package com.ebody.bip.features.wellbeing.presentation.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.usecase.SaveMoodWithAiAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val saveMoodWithAiAnalysisUseCase: SaveMoodWithAiAnalysisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    fun onEvent(event: MoodEvent) {
        when (event) {
            is MoodEvent.SelectMood -> {
                _uiState.update { it.copy(selectedMood = event.mood) }
            }

            is MoodEvent.UpdateNotes -> {
                _uiState.update { it.copy(notes = event.notes) }
            }

            is MoodEvent.UpdateDateTime -> {
                _uiState.update { it.copy(currentDateTime = event.dateTime) }
            }

            is MoodEvent.SaveMood -> {
                saveMoodRecord()
            }

            is MoodEvent.ResetSinks -> {
                _uiState.update {
                    it.copy(
                        isSavedSuccessfully = false,
                        errorMessage = null,
                        aiInstruction = "",
                        mascotExpression = MascotExpression.NORMAL
                    )
                }
            }
        }
    }

    private fun saveMoodRecord() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val currentState = _uiState.value

            if (currentState.selectedMood == null) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Selecione um nível de humor"
                    )
                }
                return@launch
            }

            val moodEntry = MoodEntry(
                level = currentState.selectedMood,
                notes = currentState.notes.orEmpty(),
                dateTime = currentState.currentDateTime
            )

            val result = runCatching {
                saveMoodWithAiAnalysisUseCase(moodEntry)
            }

            result.onSuccess { savedEntry: MoodEntry ->
                val expression = when (savedEntry.riskLevel) {
                    RiskLevel.ALERTA -> MascotExpression.CONCERNED
                    RiskLevel.CRITICO -> MascotExpression.CRITICAL
                    else -> MascotExpression.NORMAL
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSavedSuccessfully = true,
                        selectedMood = null,
                        notes = "",
                        aiInstruction = savedEntry.aiInstruction,
                        mascotExpression = expression
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.localizedMessage ?: "Erro desconhecido"
                    )
                }
            }
        }
    }
}