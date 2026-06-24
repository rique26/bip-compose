package com.ebody.bip.features.wellbeing.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WellbeingLevelsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Níveis de Bem-Estar", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                Text("😞 Mal", fontWeight = FontWeight.Bold)
                Text("• Dor intensa, febre alta, náuseas, tontura, desmaios, cansaço extremo, dificuldade de dormir, reações alérgicas graves.")

                Spacer(modifier = modifier.height(16.dp))

                Text("😐 Estranho", fontWeight = FontWeight.Bold)
                Text("• Tontura leve, fraqueza, palpitações, formigamento, desconforto digestivo, mudança de humor, confusão, sonolência, visão alterada.")

                Spacer(modifier = modifier.height(16.dp))

                Text("😊 Bem", fontWeight = FontWeight.Bold)
                Text("• Leve cansaço, desconforto passageiro, sintomas controlados, sensação de bem-estar após o medicamento.")

                Spacer(modifier = modifier.height(16.dp))

                Text("😀 Ótimo", fontWeight = FontWeight.Bold)
                Text("• Disposição elevada, ausência de sintomas, energia para atividades diárias e bem-estar pleno.")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}