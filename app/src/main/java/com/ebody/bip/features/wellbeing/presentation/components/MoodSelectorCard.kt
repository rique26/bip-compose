package com.ebody.bip.features.wellbeing.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebody.bip.R
import com.ebody.bip.features.wellbeing.presentation.MoodUiState

@Composable
fun MoodSelectorCard(
    state: MoodUiState,
    onMoodSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val moodIcons = listOf(
                    R.drawable.ic_mood_very_dissatisfied,
                    R.drawable.ic_mood_neutral,
                    R.drawable.ic_mood_satisfied,
                    R.drawable.ic_mood_very_satisfied
                )

                moodIcons.forEachIndexed { index, iconRes ->
                    val level = index + 1
                    val isSelected = state.selectedMood == level

                    val scaleAnimation by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "mood_scale_animation"
                    )

                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFE0F2F1) else Color(0xFFF8F9F9),
                        animationSpec = tween(durationMillis = 250),
                        label = "mood_bg_animation"
                    )

                    Box(
                        modifier = modifier
                            .size(56.dp)
                            .scale(scaleAnimation)
                            .clip(RoundedCornerShape(16.dp))
                            .background(backgroundColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onMoodSelected(level)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Humor $level",
                            tint = if (isSelected) Color(0xFF00897B) else Color(0xFF7F8C8D),
                            modifier = modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = modifier.height(16.dp))

            val selectedMoodText = when (state.selectedMood) {
                1 -> "Mal"
                2 -> "Estranho"
                3 -> "Bem"
                4 -> "Ótimo"
                else -> "Selecione uma opção acima"
            }

            val moodTextColor = if (state.selectedMood != null) Color(0xFF00897B) else Color(0xFF95A5A6)

            Text(
                text = selectedMoodText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = moodTextColor,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    }
}