package com.ebody.bip.features.wellbeing.presentation.mood.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ebody.bip.features.wellbeing.presentation.mood.MoodUiState
import java.time.format.DateTimeFormatter

@Composable
fun DateTimePickers(
    state: MoodUiState,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    dateInteractionSource: MutableInteractionSource,
    timeInteractionSource: MutableInteractionSource,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = state.currentDateTime.format(dateFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text("Data") },
            leadingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF00ACC1))
            },
            modifier = modifier.fillMaxWidth(),
            interactionSource = dateInteractionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00ACC1),
                unfocusedBorderColor = Color(0xFFBDC3C7),
                focusedLabelColor = Color(0xFF00ACC1),
                unfocusedLabelColor = Color(0xFF7F8C8D),
                focusedTextColor = Color(0xFF2C3E50),
                unfocusedTextColor = Color(0xFF2C3E50),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLeadingIconColor = Color(0xFF00ACC1),
                unfocusedLeadingIconColor = Color(0xFF7F8C8D)
            ),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = state.currentDateTime.format(timeFormatter),
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora") },
            leadingIcon = {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF00ACC1))
            },
            modifier = modifier.fillMaxWidth(),
            interactionSource = timeInteractionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00ACC1),
                unfocusedBorderColor = Color(0xFFBDC3C7),
                focusedLabelColor = Color(0xFF00ACC1),
                unfocusedLabelColor = Color(0xFF7F8C8D),
                focusedTextColor = Color(0xFF2C3E50),
                unfocusedTextColor = Color(0xFF2C3E50),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLeadingIconColor = Color(0xFF00ACC1),
                unfocusedLeadingIconColor = Color(0xFF7F8C8D)
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}