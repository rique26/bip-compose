package com.ebody.bip.features.wellbeing.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebody.bip.features.wellbeing.presentation.analytics.ChartItem

@Composable
fun AnalyticsDonutChart(
    items: List<ChartItem>,
    modifier: Modifier = Modifier,
    centerText: String = ""
) {
    val totalValue = remember(items) { items.map { it.value }.sum() }

    // Animação de preenchimento do gráfico
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(items) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val strokeWidth = with(LocalDensity.current) { 32.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .semantics { contentDescription = "Gráfico de distribuição de humor" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            var startAngle = -90f // Inicia a partir do topo
            val diameter = size.minDimension - strokeWidth
            val topLeftOffset = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            items.forEach { item ->
                if (totalValue > 0f) {
                    val sweepAngle = (item.value / totalValue) * 360f * animationProgress.value

                    drawArc(
                        color = Color(android.graphics.Color.parseColor(item.colorHex)),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        if (centerText.isNotBlank()) {
            Text(
                text = centerText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}