package com.ebody.bip.features.schedule.presentation.medication_schedule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebody.bip.features.schedule.presentation.medication_schedule.components.MedicationScheduleContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScheduleScreen(
    medicationIds: List<Long>,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: MedicationScheduleViewModel = hiltViewModel()
) {
    LaunchedEffect(medicationIds) {
        viewModel.loadMedications(medicationIds)
    }

    val medications by viewModel.medications.collectAsState()
    val scheduleTimes = remember { mutableStateListOf(Pair(8, 0)) }
    var dosage by remember { mutableStateOf("") }

    var showDosageDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }

    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0)

    MedicationScheduleContent(
        medications = medications,
        dosage = dosage,
        scheduleTimes = scheduleTimes,
        timePickerState = timePickerState,
        showDosageDialog = showDosageDialog,
        showTimePicker = showTimePicker,
        onBack = onBack,
        onSaveClick = {
            if (dosage.isNotBlank()) {
                viewModel.saveReminders(
                    scheduleTimes = scheduleTimes,
                    dosage = dosage,
                    onSuccess = onFinish
                )
            }
        },
        onAddSchedule = { scheduleTimes.add(Pair(8, 0)) },
        onRemoveSchedule = { index -> scheduleTimes.removeAt(index) },
        onScheduleClick = { index, horario ->
            editingIndex = index
            timePickerState.hour = horario.first
            timePickerState.minute = horario.second
            showTimePicker = true
        },
        onDosageClick = { showDosageDialog = true },
        onDismissDosage = { showDosageDialog = false },
        onConfirmDosage = { selectedDosage ->
            dosage = selectedDosage
            showDosageDialog = false
        },
        onDismissTime = { showTimePicker = false },
        onConfirmTime = {
            scheduleTimes[editingIndex] = Pair(timePickerState.hour, timePickerState.minute)
            showTimePicker = false
        }
    )
}