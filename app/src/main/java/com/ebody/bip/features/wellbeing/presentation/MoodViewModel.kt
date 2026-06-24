package br.com.yourpackage.features.wellbeing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.usecase.SaveMoodUseCase
import com.ebody.bip.features.wellbeing.presentation.MoodEvent
import com.ebody.bip.features.wellbeing.presentation.MoodUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val saveMoodUseCase: SaveMoodUseCase
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
                    it.copy(isSavedSuccessfully = false, errorMessage = null)
                }
            }
        }
    }

    private fun saveMoodRecord() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                val currentState = _uiState.value
                val moodEntry = MoodEntry(
                    level = currentState.selectedMood ?: return@runCatching,
                    notes = currentState.notes.orEmpty(),
                    dateTime = currentState.currentDateTime
                )
                saveMoodUseCase(moodEntry)
            }.onSuccess {
                _uiState.update {
                    it.copy(isSaving = false, isSavedSuccessfully = true, selectedMood = null, notes = "")
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.localizedMessage ?: "Erro desconhecido")
                }
            }
        }
    }
}