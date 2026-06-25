package com.ebody.bip.features.wellbeing.presentation.mood.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BoxScope.MoodHeader(
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onInfoClick,
        modifier = modifier.align(Alignment.TopEnd)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Informações",
            tint = Color(0xFF7F8C8D)
        )
    }
}