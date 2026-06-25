package com.ebody.bip.features.wellbeing.presentation.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.model.TimeFilter
import com.ebody.bip.features.wellbeing.presentation.history.components.MoodHistoryItem

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    currentFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    onEntryClick: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
            Text(
                text = "Seus Registros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = currentFilter == filter,
                        onClick = { onFilterSelected(filter) },
                        label = { Text(filter.label) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conteúdo Principal
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                uiState.records.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Nenhum registro ainda", style = MaterialTheme.typography.titleMedium)
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
                    ) {
                        items(uiState.records, key = { it.id }) { entry ->
                            MoodHistoryItem(
                                entry = entry,
                                onClick = { clickedEntry ->
                                    onEntryClick(clickedEntry)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}