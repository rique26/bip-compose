package com.ebody.bip.features.schedule.presentation.medication_selection.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.presentation.medication_selection.MedicationSelectionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationSelectionContent(
    onNavigateToSchedule: () -> Unit,
    state: MedicationSelectionUiState,
    medications: List<Medication>,
    query: String,
    selectedIds: Set<Long>,
    onQueryChange: (String) -> Unit,
    onSelectionChange: (Long, Boolean) -> Unit,
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Camada 1: A Lista (fica por baixo)
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // --- HEADER ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Medicamentos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (state) {
                    is MedicationSelectionUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is MedicationSelectionUiState.Success -> {
                        if (medications.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Nenhum medicamento encontrado", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 120.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                userScrollEnabled = true
                            ) {
                                items(
                                    items = medications,
                                    key = { it.id }
                                ) { medication ->
                                    MedicationRow(
                                        medication = medication,
                                        isSelected = selectedIds.contains(medication.id),
                                        onCheckedChange = { isSelected ->
                                            onSelectionChange(medication.id, isSelected)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }

            // Camada 2: Botões Fixos (ficam por cima)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { if (selectedIds.isNotEmpty()) onNavigateToSchedule() },
                    modifier = Modifier.width(140.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("AVANÇAR", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { /* Ação "Não encontrei" */ },
                    modifier = Modifier.width(140.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1D1D1))
                ) {
                    Text("Não encontrei", color = Color.Black)
                }
            }
        }
    }
}