package com.ebody.bip.features.schedule.presentation.home_dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.schedule.domain.model.MedicationReminder
import com.ebody.bip.features.schedule.presentation.home_dashboard.HomeDashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardContent(
    uiState: HomeDashboardUiState,
    isRefreshing: Boolean,
    onNavigateToMedicationSelection: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMood: () -> Unit,
    onDeleteReminder: (MedicationReminder) -> Unit,
    onRefresh: () -> Unit
) {
    val systemBarPaddings = WindowInsets.systemBars.asPaddingValues()
    var itemToDelete by remember { mutableStateOf<MedicationReminder?>(null) }

    itemToDelete?.let { reminder ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(text = "Excluir Alarme") },
            text = { Text(text = "Tem certeza que deseja excluir o alarme de ${reminder.medication.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteReminder(reminder)
                        itemToDelete = null
                    }
                ) {
                    Text(text = "Excluir", color = Color(0xFFC62828))
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->

        // PullToRefreshBox (M3 nativo) executa a sincronização com a nuvem puxando a tela para baixo
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 24.dp + systemBarPaddings.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    Box {
                        RoundedHeaderBackground(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                        )

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            HeaderContent(modifier = Modifier.padding(top = 32.dp))
                            ActionButtonsRow(
                                modifier = Modifier.padding(vertical = 16.dp),
                                onNavigateToMedicationSelection = onNavigateToMedicationSelection,
                                onNavigateToEmergency = onNavigateToEmergency,
                                onNavigateToMood = onNavigateToMood
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Alarmes de medicamentos",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }

                items(uiState.reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { itemToDelete = reminder }
                    )
                }
            }
        }
    }
}