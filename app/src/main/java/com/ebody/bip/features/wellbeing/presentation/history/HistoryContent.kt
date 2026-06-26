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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import com.ebody.bip.features.wellbeing.domain.model.TimeFilter
import com.ebody.bip.features.wellbeing.presentation.history.components.MoodHistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    currentFilter: TimeFilter,
    isRefreshing: Boolean,
    onFilterSelected: (TimeFilter) -> Unit,
    onEntryClick: (MoodEntry) -> Unit,
    onRefresh: () -> Unit,
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                    uiState.records.isEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(120.dp))
                                Text(
                                    text = "Nenhum registro encontrado.",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Puxe para baixo para sincronizar com a nuvem.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
}