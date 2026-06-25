package com.ebody.bip.features.auth.presentation.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ebody.bip.core.presentation.components.TextField
import com.ebody.bip.core.presentation.util.InputMasks
import com.ebody.bip.core.presentation.util.PhoneVisualTransformation

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
            placeholder = "mail@email.com",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = phone,
            onValueChange = { newValue ->
                val digitsOnly = newValue.filter { it.isDigit() }.take(11)
                onPhoneChange(digitsOnly)
            },
            label = "Telefone",
            placeholder = "(85) 99999-9999",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PhoneVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
