package com.ebody.bip.features.schedule.presentation.home_dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ActionButtonsRow(
    modifier: Modifier = Modifier,
    onNavigateToMedicationSelection: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMood: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionCard(
            "Agendar",
            Icons.Default.DateRange,
            Modifier
                .weight(1f)
                .padding(bottom = 36.dp),
            onClick = onNavigateToMedicationSelection
        )

        ActionCard(
            "Controle",
            Icons.Default.Timeline,
            Modifier.weight(1f),
            onNavigateToMood
        )

        ActionCard(
            "Emergência",
            Icons.Default.Warning,
            Modifier
                .weight(1f)
                .padding(bottom = 36.dp),
            onClick = onNavigateToEmergency
        )
    }
}
