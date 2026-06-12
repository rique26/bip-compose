package com.ebody.bip.features.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ebody.bip.core.presentation.components.TextField

@Composable
fun RegisterStep1(
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    birthDate: String, onBirthDateChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = "Nome",
            placeholder = "Nome"
        )

        TextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = "Sobrenome",
            placeholder = "Sobrenome"
        )

        TextField(
            value = birthDate,
            onValueChange = onBirthDateChange,
            label = "Data de nascimento",
            placeholder = "DD/MM/AAAA",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}