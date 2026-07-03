package com.ebody.bip.features.wellbeing.presentation.mood.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebody.bip.features.wellbeing.presentation.mood.MascotExpression

@Composable
fun AiFeedbackCard(
    instruction: String,
    expression: MascotExpression,
    modifier: Modifier = Modifier
) {
    // Definição dinâmica de cores e ícones baseados no estado do mascote
    val (backgroundColor, strokeColor, icon, titleText) = when (expression) {
        MascotExpression.CRITICAL -> Quadruple(
            Color(0xFFFDF2F2), // Vermelho bem suave
            Color(0xFFF8D7DA),
            Icons.Default.Warning,
            "Aviso Crítico"
        )
        MascotExpression.CONCERNED -> Quadruple(
            Color(0xFFFFF9F2), // Amarelo/Laranja bem suave
            Color(0xFFFEEBC8),
            Icons.Default.Warning,
            "Atenção"
        )
        MascotExpression.NORMAL -> Quadruple(
            Color(0xFFF3FBF7), // Verde bem suave
            Color(0xFFC6F6D5),
            Icons.Default.CheckCircle,
            "Análise Concluída"
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, strokeColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when (expression) {
                        MascotExpression.CRITICAL -> Color(0xFFC53030)
                        MascotExpression.CONCERNED -> Color(0xFFDD6B20)
                        MascotExpression.NORMAL -> Color(0xFF2F855A)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titleText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = instruction,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color(0xFF4A5568)
            )
        }
    }
}

// Helper simples para organizar os retornos no when
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)