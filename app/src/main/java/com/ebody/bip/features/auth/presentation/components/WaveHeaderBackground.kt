package com.ebody.bip.features.auth.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Componente que desenha o header com gradiente em forma de onda
 * Replicando o VectorDrawable header_wave_bg
 */
@Composable
fun WaveHeaderBackground(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier,
        onDraw = {
            drawWaveGradient()
        }
    )
}

fun DrawScope.drawWaveGradient() {
    val width = size.width
    val height = size.height

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF29B6F6),
            Color(0xFF239BD1),
            Color(0xFF1E84B1)
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, height)
    )

    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(width, 0f)
        lineTo(width, height * 0.8125f)
        cubicTo(
            x1 = width * 0.75f,
            y1 = height,
            x2 = width * 0.25f,
            y2 = height * 0.625f,
            x3 = 0f,
            y3 = height * 0.8125f
        )
        close()
    }

    drawPath(path, brush = gradientBrush)
}