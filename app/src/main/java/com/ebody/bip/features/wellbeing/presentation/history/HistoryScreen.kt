package com.ebody.bip.features.wellbeing.presentation.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToMoodEntry: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    var selectedEntryDetails by remember { mutableStateOf<MoodEntry?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Histórico", style = MaterialTheme.typography.titleLarge)
                },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.Insights, contentDescription = "Estatísticas")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToMoodEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Novo Registro")
            }
        }
    ) { paddingValues ->
        HistoryContent(
            uiState = uiState,
            currentFilter = currentFilter,
            isRefreshing = isRefreshing,
            onFilterSelected = viewModel::onFilterSelected,
            onEntryClick = { entry ->
                selectedEntryDetails = entry
            },
            onRefresh = {
                viewModel.refreshData()
            },
            modifier = Modifier.padding(paddingValues)
        )
    }

    selectedEntryDetails?.let { entry ->
        val formattedDate = entry.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))

        AlertDialog(
            onDismissRequest = { selectedEntryDetails = null },
            title = { Text(text = "Detalhes do Registro") },
            text = {
                Column {
                    Text(text = "Data: $formattedDate", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Observações:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.notes.ifBlank { "Nenhuma observação informada." },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedEntryDetails = null }) {
                    Text("Fechar")
                }
            }
        )
    }
}

