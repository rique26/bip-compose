package com.ebody.bip.features.schedule.presentation.medication_selection

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebody.bip.features.schedule.presentation.medication_selection.components.MedicationSelectionContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSelectionScreen(
    onNavigateToSchedule: (List<Long>) -> Unit,
    viewModel: MedicationSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    MedicationSelectionContent(
        onNavigateToSchedule = {
            onNavigateToSchedule(state.selectedIds.toList())
        },
        state = state,
        onQueryChange = { newQuery ->
            viewModel.onEvent(MedicationSelectionEvent.SearchQueryChanged(newQuery))
        },
        onSelectionChange = { id, isSelected ->
            viewModel.onEvent(MedicationSelectionEvent.ToggleMedicationSelection(id, isSelected))
        }
    )
}