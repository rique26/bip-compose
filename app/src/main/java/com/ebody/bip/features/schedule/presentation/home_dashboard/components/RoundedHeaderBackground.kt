package com.ebody.bip.features.schedule.presentation.home_dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun RoundedHeaderBackground(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier,
        onDraw = {
            drawRoundedHeaderGradient()
        }
    )
}

fun DrawScope.drawRoundedHeaderGradient() {
    val width = size.width
    val height = size.height
    val cornerRadius = 32.dp.toPx()

    val gradientBrush = Brush.verticalGradient(
        0.0f to Color(0xFF29B6F6),
        0.85f to Color(0xFF239BD1),
        1.0f to Color(0xFF1E84B1),
        startY = 0f,
        endY = height
    )

    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(width, 0f)
        lineTo(width, height - cornerRadius)
        quadraticTo(
            x1 = width,
            y1 = height,
            x2 = width - cornerRadius,
            y2 = height
        )
        lineTo(cornerRadius, height)
        quadraticTo(
            x1 = 0f,
            y1 = height,
            x2 = 0f,
            y2 = height - cornerRadius
        )
        close()
    }

    drawPath(path, brush = gradientBrush)
}