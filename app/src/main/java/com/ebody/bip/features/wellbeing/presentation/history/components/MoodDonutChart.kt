package com.ebody.bip.features.wellbeing.presentation.history.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MoodDonutChart(
    moodCounts: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val total = moodCounts.values.sum().toFloat()
    if (total == 0f) return

    // Cores baseadas nos seus níveis
    val colorMap = mapOf(
        1 to Color(0xFF2ECC71), // Ótimo (Verde)
        2 to Color(0xFFF1C40F), // Bem (Amarelo)
        3 to Color(0xFFE74C3C), // Mal (Vermelho)
        4 to Color(0xFF95A5A6)  // Estranho (Cinza)
    )

    Canvas(modifier = modifier.size(200.dp)) {
        var startAngle = -90f

        moodCounts.forEach { (level, count) ->
            val sweepAngle = (count / total) * 360f
            drawArc(
                color = colorMap[level] ?: Color.Gray,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
        }
    }
}