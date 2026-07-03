package com.ebody.bip.features.wellbeing.presentation.mood

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebody.bip.features.wellbeing.presentation.mood.components.AiFeedbackCard
import com.ebody.bip.features.wellbeing.presentation.mood.components.DateTimePickers
import com.ebody.bip.features.wellbeing.presentation.mood.components.MoodHeader
import com.ebody.bip.features.wellbeing.presentation.mood.components.MoodSelectorCard
import com.ebody.bip.features.wellbeing.presentation.mood.components.SaveMoodButton
import com.ebody.bip.features.wellbeing.presentation.mood.components.WellbeingNotesField
import java.time.format.DateTimeFormatter

@Composable
fun MoodContent(
    state: MoodUiState,
    dateFormatter: DateTimeFormatter,
    timeFormatter: DateTimeFormatter,
    dateInteractionSource: MutableInteractionSource,
    timeInteractionSource: MutableInteractionSource,
    onMoodSelected: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding(),
        color = Color(0xFFF4F6F6)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Como você está se sentindo?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34495E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                MoodSelectorCard(
                    state = state,
                    onMoodSelected = onMoodSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                DateTimePickers(
                    state = state,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    dateInteractionSource = dateInteractionSource,
                    timeInteractionSource = timeInteractionSource
                )

                Spacer(modifier = Modifier.height(20.dp))

                WellbeingNotesField(
                    notes = state.notes ?: "",
                    onNotesChange = onNotesChange
                )

                Spacer(modifier = Modifier.height(28.dp))

                SaveMoodButton(
                    isEnabled = state.selectedMood != null,
                    isSaving = state.isSaving,
                    onClick = onSaveClick
                )

                if (state.aiInstruction.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    AiFeedbackCard(
                        instruction = state.aiInstruction,
                        expression = state.mascotExpression
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            MoodHeader(
                onInfoClick = { onInfoClick() },
                modifier = Modifier.padding(top = 24.dp)
            )

        }
    }
}