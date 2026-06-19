package com.ebody.bip.features.schedule.presentation.medication_schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebody.bip.features.schedule.domain.model.Medication
import com.ebody.bip.features.schedule.presentation.medication_schedule.components.DosagePickerDialog
import com.ebody.bip.features.schedule.presentation.medication_schedule.components.ScheduleTimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScheduleContent(
    medications: List<Medication>,
    dosage: String,
    scheduleTimes: List<Pair<Int, Int>>,
    timePickerState: TimePickerState,
    showDosageDialog: Boolean,
    showTimePicker: Boolean,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onAddSchedule: () -> Unit,
    onRemoveSchedule: (Int) -> Unit,
    onScheduleClick: (Int, Pair<Int, Int>) -> Unit,
    onDosageClick: () -> Unit,
    onDismissDosage: () -> Unit,
    onConfirmDosage: (String) -> Unit,
    onDismissTime: () -> Unit,
    onConfirmTime: () -> Unit
) {
    val dosageInteractionSource = remember { MutableInteractionSource() }
    val isDosagePressed by dosageInteractionSource.collectIsPressedAsState()

    LaunchedEffect(isDosagePressed) {
        if (isDosagePressed) onDosageClick()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Definir Horários e Dosagem",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dosagem / Quantidade") },
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = dosageInteractionSource
                    )

                    Text(
                        text = "Horários (${scheduleTimes.size}/5)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(scheduleTimes) { index, horario ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onScheduleClick(index, horario)
                                        }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val hourStr = horario.first.toString().padStart(2, '0')
                                    val minuteStr = horario.second.toString().padStart(2, '0')

                                    Text(
                                        text = "$hourStr:$minuteStr",
                                        style = MaterialTheme.typography.headlineSmall
                                    )

                                    if (scheduleTimes.size > 1) {
                                        IconButton(onClick = { onRemoveSchedule(index) }) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remover horário",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            if (scheduleTimes.size < 5) {
                                Button(
                                    onClick = onAddSchedule,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Adicionar horário", color = Color.Black)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E84B1)),
                    enabled = dosage.isNotBlank() && medications.isNotEmpty()
                ) {
                    Text(
                        "SALVAR PARA ${medications.size} MEDICAMENTOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            if (showDosageDialog) {
                DosagePickerDialog(
                    onDismiss = onDismissDosage,
                    onConfirm = onConfirmDosage
                )
            }

            if (showTimePicker) {
                ScheduleTimePickerDialog(
                    timePickerState = timePickerState,
                    onDismiss = onDismissTime,
                    onConfirm = onConfirmTime
                )
            }
        }
    }
}