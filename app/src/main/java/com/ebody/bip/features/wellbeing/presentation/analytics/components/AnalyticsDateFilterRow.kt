package com.ebody.bip.features.wellbeing.presentation.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.wellbeing.presentation.analytics.AnalyticsTimeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDateFilterRow(
    selectedFilter: AnalyticsTimeFilter,
    onFilterSelected: (AnalyticsTimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    var tempStartDate by remember { mutableStateOf<LocalDate?>(null) }

    val options = listOf("3d", "7d", "30d", "Outro")

    val selectedIndex = when (selectedFilter) {
        is AnalyticsTimeFilter.Days -> when (selectedFilter.amount) {
            3 -> 0
            7 -> 1
            30 -> 2
            else -> 1
        }
        is AnalyticsTimeFilter.Custom -> 3
    }

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                onClick = {
                    when (index) {
                        0 -> onFilterSelected(AnalyticsTimeFilter.Days(3))
                        1 -> onFilterSelected(AnalyticsTimeFilter.Days(7))
                        2 -> onFilterSelected(AnalyticsTimeFilter.Days(30))
                        3 -> showStartPicker = true // Inicia o fluxo profissional de data customizada
                    }
                },
                selected = selectedIndex == index,
                label = { Text(text = label, maxLines = 1) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size)
            )
        }
    }

    // 1. Diálogo para Data de Início
    if (showStartPicker) {
        val startPickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startPickerState.selectedDateMillis?.let { millis ->
                            tempStartDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            showStartPicker = false
                            showEndPicker = true // Abre ato contínuo a data de término
                        }
                    }
                ) { Text("Avançar") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = startPickerState,
                title = { Text(text = "Data de Início", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) },
                showModeToggle = false
            )
        }
    }

    // 2. Diálogo para Data de Término
    if (showEndPicker) {
        val endPickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endPickerState.selectedDateMillis?.let { millis ->
                            val endDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()

                            tempStartDate?.let { startDate ->
                                onFilterSelected(AnalyticsTimeFilter.Custom(startDate, endDate))
                            }
                            showEndPicker = false
                        }
                    }
                ) { Text("Filtrar") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = endPickerState,
                title = { Text(text = "Data de Término", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) },
                showModeToggle = false
            )
        }
    }
}