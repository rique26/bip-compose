package com.ebody.bip.features.wellbeing.presentation.analytics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
    var showDateRangePicker by remember { mutableStateOf(false) }
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

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    when (index) {
                        0 -> onFilterSelected(AnalyticsTimeFilter.Days(3))
                        1 -> onFilterSelected(AnalyticsTimeFilter.Days(7))
                        2 -> onFilterSelected(AnalyticsTimeFilter.Days(30))
                        3 -> showDateRangePicker = true
                    }
                },
                selected = selectedIndex == index,
                label = { Text(text = label, maxLines = 1) },
                icon = {
                    if (index == 3) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    } else {
                        SegmentedButtonDefaults.Icon(active = selectedIndex == index)
                    }
                }
            )
        }
    }

    // Date Range Picker nativo para intervalo de datas
    if (showDateRangePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDateRangePicker = false
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            val startDate = Instant.ofEpochMilli(startMillis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            val endDate = Instant.ofEpochMilli(endMillis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()

                            onFilterSelected(AnalyticsTimeFilter.Custom(startDate, endDate))
                        }
                    },
                    enabled = dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}