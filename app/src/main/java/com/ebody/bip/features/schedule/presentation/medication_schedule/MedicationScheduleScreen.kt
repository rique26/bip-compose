package com.ebody.bip.features.schedule.presentation.medication_schedule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(medicationIds) {
        viewModel.onEvent(MedicationScheduleEvent.LoadMedications(medicationIds))
    }

    val timePickerState = rememberTimePickerState(
        initialHour = state.selectedHour,
        initialMinute = state.selectedMinute
    )

    // Atualiza o estado interno do TimePicker se alterado via fluxo de UI
    LaunchedEffect(state.selectedHour, state.selectedMinute) {
        timePickerState.hour = state.selectedHour
        timePickerState.minute = state.selectedMinute
    }

    MedicationScheduleContent(
        state = state,
        timePickerState = timePickerState,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onFinish = onFinish
    )
}