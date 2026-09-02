package com.ebody.bip.features.schedule.presentation.medication_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.domain.usecase.GetMedicationByIdUseCase
import com.ebody.bip.features.schedule.domain.usecase.SaveReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import kotlin.random.Random

data class MedicationScheduleUiState(
    val medications: List<Medication> = emptyList(),
    val dosage: String = "",
    val scheduleTimes: List<Pair<Int, Int>> = listOf(Pair(8, 0)),
    val isSaving: Boolean = false,
    val showDosageDialog: Boolean = false,
    val showTimePicker: Boolean = false,
    val editingIndex: Int = -1,
    val selectedHour: Int = 8,
    val selectedMinute: Int = 0
)

sealed interface MedicationScheduleEvent {
    data class LoadMedications(val ids: List<Long>) : MedicationScheduleEvent
    data class UpdateDosage(val dosage: String) : MedicationScheduleEvent
    data class AddScheduleTime(val time: Pair<Int, Int> = Pair(8, 0)) : MedicationScheduleEvent
    data class RemoveScheduleTime(val index: Int) : MedicationScheduleEvent
    data class OpenTimePicker(val index: Int, val time: Pair<Int, Int>) : MedicationScheduleEvent
    data class ConfirmTimePicker(val hour: Int, val minute: Int) : MedicationScheduleEvent
    data class ToggleDosageDialog(val show: Boolean) : MedicationScheduleEvent
    data class ToggleTimePicker(val show: Boolean) : MedicationScheduleEvent
    data class SaveReminders(val onSuccess: () -> Unit) : MedicationScheduleEvent
}

@HiltViewModel
class MedicationScheduleViewModel @Inject constructor(
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val saveReminderUseCase: SaveReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationScheduleUiState())
    val uiState: StateFlow<MedicationScheduleUiState> = _uiState.asStateFlow()

    fun onEvent(event: MedicationScheduleEvent) {
        when (event) {
            is MedicationScheduleEvent.LoadMedications -> loadMedications(event.ids)
            is MedicationScheduleEvent.UpdateDosage -> updateDosage(event.dosage)
            is MedicationScheduleEvent.AddScheduleTime -> addScheduleTime()
            is MedicationScheduleEvent.RemoveScheduleTime -> removeScheduleTime(event.index)
            is MedicationScheduleEvent.OpenTimePicker -> openTimePicker(event.index, event.time)
            is MedicationScheduleEvent.ConfirmTimePicker -> confirmTimePicker(event.hour, event.minute)
            is MedicationScheduleEvent.ToggleDosageDialog -> toggleDosageDialog(event.show)
            is MedicationScheduleEvent.ToggleTimePicker -> toggleTimePicker(event.show)
            is MedicationScheduleEvent.SaveReminders -> saveReminders(event.onSuccess)
        }
    }
    private fun loadMedications(ids: List<Long>) {
        viewModelScope.launch {
            val loadedMedications = ids.mapNotNull { id ->
                getMedicationByIdUseCase(id)
            }
            _uiState.update { it.copy(medications = loadedMedications) }
        }
    }

    private fun updateDosage(dosage: String) {
        _uiState.update { it.copy(dosage = dosage, showDosageDialog = false) }
    }

    private fun addScheduleTime() {
        _uiState.update { state ->
            if (state.scheduleTimes.size < 5) {
                state.copy(scheduleTimes = state.scheduleTimes + Pair(8, 0))
            } else state
        }
    }

    private fun removeScheduleTime(index: Int) {
        _uiState.update { state ->
            if (state.scheduleTimes.size > 1) {
                val updatedTimes = state.scheduleTimes.toMutableList().apply { removeAt(index) }
                state.copy(scheduleTimes = updatedTimes)
            } else state
        }
    }

    private fun openTimePicker(index: Int, time: Pair<Int, Int>) {
        _uiState.update {
            it.copy(
                editingIndex = index,
                selectedHour = time.first,
                selectedMinute = time.second,
                showTimePicker = true
            )
        }
    }

    private fun confirmTimePicker(hour: Int, minute: Int) {
        _uiState.update { state ->
            if (state.editingIndex in state.scheduleTimes.indices) {
                val updatedTimes = state.scheduleTimes.toMutableList().apply {
                    this[state.editingIndex] = Pair(hour, minute)
                }
                state.copy(scheduleTimes = updatedTimes, showTimePicker = false, editingIndex = -1)
            } else {
                state.copy(showTimePicker = false)
            }
        }
    }

    private fun toggleDosageDialog(show: Boolean) {
        _uiState.update { it.copy(showDosageDialog = show) }
    }

    private fun toggleTimePicker(show: Boolean) {
        _uiState.update { it.copy(showTimePicker = show) }
    }

    private fun saveReminders(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.medications.isEmpty() || state.dosage.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                for (med in state.medications) {
                    val reminders = state.scheduleTimes.map { (hour, minute) ->
                        MedicationReminder(
                            medication = med,
                            time = LocalTime.of(hour, minute),
                            dosage = state.dosage,
                            createdAt = System.currentTimeMillis(),
                            requestCode = Random.nextInt(10001)
                        )
                    }

                    reminders.forEach { reminder ->
                        saveReminderUseCase(reminder)
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                //todo
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}