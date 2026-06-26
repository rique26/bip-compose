package com.ebody.bip.features.schedule.presentation.home_dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.presentation.home_dashboard.components.HomeDashboardContent
import java.time.LocalTime

@Composable
fun HomeDashboardScreen(
    onNavigateToMedicationSelection: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMood: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    HomeDashboardContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onNavigateToMedicationSelection = onNavigateToMedicationSelection,
        onNavigateToEmergency = onNavigateToEmergency,
        onNavigateToMood = onNavigateToMood,
        onDeleteReminder = { reminder ->
            viewModel.onEvent(HomeDashboardEvent.DeleteReminder(reminder))
        },
        onRefresh = {
            viewModel.onEvent(HomeDashboardEvent.Refresh)
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeDashboard() {
    val mockReminders = listOf(
        MedicationReminder(
            id = 1,
            medication = Medication(1, "Cloridrato de Nafazolina"),
            dosage = "2 Comprimidos",
            time = LocalTime.of(14, 20),
            createdAt = System.currentTimeMillis(),
            requestCode = 100
        ),
        MedicationReminder(
            id = 2,
            medication = Medication(2, "Bromopan"),
            dosage = "5 mg",
            time = LocalTime.of(16, 10),
            createdAt = System.currentTimeMillis(),
            requestCode = 101
        ),
        MedicationReminder(
            id = 3,
            medication = Medication(3, "Simeticona, cloridrato..."),
            dosage = "2 Comprimidos",
            time = LocalTime.of(18, 0),
            createdAt = System.currentTimeMillis(),
            requestCode = 102
        )
    )

    MaterialTheme {
        HomeDashboardContent(
            uiState = HomeDashboardUiState(reminders = mockReminders),
            isRefreshing = false,
            onNavigateToMedicationSelection = {},
            onNavigateToEmergency = {},
            onNavigateToMood = {},
            onDeleteReminder = {},
            onRefresh = {}
        )
    }
}