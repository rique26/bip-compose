package com.ebody.bip.features.wellbeing.presentation.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebody.bip.R
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import java.time.format.DateTimeFormatter

@Composable
fun MoodHistoryItem(
    entry: MoodEntry,
    onClick: (MoodEntry) -> Unit
) {
    val moodLabel = when (entry.level) {
        1 -> "Ótimo"
        2 -> "Bem"
        3 -> "Mal"
        4 -> "Estranho"
        else -> "Estado"
    }

    val iconRes = when (entry.level) {
        1 -> R.drawable.ic_mood_very_satisfied
        2 -> R.drawable.ic_mood_satisfied
        3 -> R.drawable.ic_mood_very_dissatisfied
        4 -> R.drawable.ic_mood_neutral
        else -> R.drawable.ic_mood_neutral
    }

    val formattedDate = entry.dateTime.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(entry) }
            .padding(horizontal = 8.dp),
        headlineContent = {
            Text(moodLabel, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text(formattedDate)
        },
        leadingContent = {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}