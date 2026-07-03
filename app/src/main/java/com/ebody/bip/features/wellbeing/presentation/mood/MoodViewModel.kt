package com.ebody.bip.features.wellbeing.presentation.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.intelligence.model.RiskLevel
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.usecase.SaveMoodWithAiAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.ebody.bip.core.domain.util.Result
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
            is MoodEvent.SelectMood -> { _uiState.update { it.copy(selectedMood = event.mood) } }

            is MoodEvent.UpdateNotes -> { _uiState.update { it.copy(notes = event.notes) } }

            is MoodEvent.UpdateDateTime -> { _uiState.update { it.copy(currentDateTime = event.dateTime) } }

            is MoodEvent.SaveMood -> { saveMoodRecord() }

            is MoodEvent.ResetSinks -> resetUiState()
        }
    }

    private fun saveMoodRecord() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val moodEntry = MoodEntry(
                level = currentState.selectedMood,
                notes = currentState.notes.orEmpty(),
                dateTime = currentState.currentDateTime
            )

            when (val result = saveMoodWithAiAnalysisUseCase(moodEntry)) {
                is Result.Success -> {
                    val savedEntry = result.data
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSavedSuccessfully = true,
                            selectedMood = currentState.selectedMood,
                            notes = currentState.notes,
                            aiInstruction = savedEntry.aiInstruction,
                            mascotExpression = savedEntry.riskLevel.toMascotExpression()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.error.localizedMessage ?: "Erro desconhecido"
                        )
                    }
                }
            }
        }
    }

    private fun resetUiState() {
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