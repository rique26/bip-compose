package com.ebody.bip.features.schedule.presentation.medication_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ebody.bip.features.schedule.domain.model.Medication

@Composable
fun MedicationListScreen(
    navController: NavController,
    viewModel: MedicationListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val medications by viewModel.medications.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }

    LaunchedEffect(Unit) {
        viewModel.loadMedications()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Camada 1: A Lista (fica por baixo)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // --- HEADER COM BOTÃO DE LOGOUT ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medicamentos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Botão de Logout para teste
                Text(
                    text = "Sair",
                    color = Color.Red,
                    modifier = Modifier
                        .clickable {}
                        .padding(8.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            SearchBar(
                query = searchQuery,
                onQueryChange = { newQuery ->
                    viewModel.onSearchQueryChanged(newQuery)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is MedicationListUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MedicationListUiState.Success -> {
                    if (medications.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhum medicamento encontrado", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(medications) { medication ->
                                MedicationRow(
                                    medication = medication,
                                    isSelected = selectedIds.contains(medication.id),
                                    onCheckedChange = { isSelected ->
                                        selectedIds = if (isSelected) selectedIds + medication.id else selectedIds - medication.id
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
                .align(Alignment.BottomEnd) // Fixa no canto inferior direito
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {},
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        placeholder = { Text("Pesquisar medicamento", color = Color.Gray) },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        // Versão corrigida para Material 3
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            cursorColor = Color.Gray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun MedicationRow(
    medication: Medication,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isSelected) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Medication,
            contentDescription = null,
            tint = Color(0xFF90CAF9),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = medication.name.uppercase(),
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = Color.DarkGray,
            letterSpacing = 1.sp
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF64B5F6))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationListPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        // Mock simples para o preview
        MedicationListScreen(navController = navController)
    }
}