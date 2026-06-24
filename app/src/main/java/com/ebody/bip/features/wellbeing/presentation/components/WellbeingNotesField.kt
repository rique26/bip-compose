package com.ebody.bip.features.wellbeing.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WellbeingNotesField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Detalhes e observações") },
        placeholder = { Text("Descreva brevemente o que aconteceu...") },
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00ACC1),
            unfocusedBorderColor = Color(0xFFBDC3C7),
            focusedLabelColor = Color(0xFF00ACC1),
            unfocusedLabelColor = Color(0xFF7F8C8D),
            focusedTextColor = Color(0xFF2C3E50),
            unfocusedTextColor = Color(0xFF2C3E50),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}