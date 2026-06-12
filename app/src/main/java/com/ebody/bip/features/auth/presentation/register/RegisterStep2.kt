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
fun RegisterStep2(
    email: String, onEmailChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = "E-mail",
            placeholder = "exemplo@email.com",
            keyboardType = KeyboardType.Email
        )
        TextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Telefone",
            placeholder = "(85) 99999-9999",
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
