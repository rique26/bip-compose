package com.ebody.bip.features.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebody.bip.core.presentation.components.TextField

@Composable
fun RegisterStep3(
    password: String, onPasswordChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Senha",
            placeholder = "Crie uma senha forte",
            keyboardType = KeyboardType.Password
        )
        Text(
            text = "Sua senha deve conter pelo menos 8 caracteres.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

