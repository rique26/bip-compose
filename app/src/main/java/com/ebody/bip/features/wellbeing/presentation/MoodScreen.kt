package com.ebody.bip.features.wellbeing.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import br.com.yourpackage.features.wellbeing.presentation.MoodViewModel
import com.ebody.bip.features.wellbeing.presentation.components.WellbeingLevelsDialog
import java.time.format.DateTimeFormatter

@Composable
fun MoodScreen(
    viewModel: MoodViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val dateInteractionSource = remember { MutableInteractionSource() }
    val isDatePressed by dateInteractionSource.collectIsPressedAsState()

    val timeInteractionSource = remember { MutableInteractionSource() }
    val isTimePressed by timeInteractionSource.collectIsPressedAsState()

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        Log.d("MoodScreenDebug", "Exibindo WellbeingLevelsDialog na tela.")
        WellbeingLevelsDialog(onDismiss = {
            Log.d("MoodScreenDebug", "Fechando diálogo (onDismiss acionado).")
            showDialog = false
        })
    }

    val datePickerDialog = remember(state.currentDateTime) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = state.currentDateTime
                    .withYear(year)
                    .withMonth(month + 1)
                    .withDayOfMonth(dayOfMonth)
                viewModel.onEvent(MoodEvent.UpdateDateTime(newDate))
            },
            state.currentDateTime.year,
            state.currentDateTime.monthValue - 1,
            state.currentDateTime.dayOfMonth
        )
    }

    val timePickerDialog = remember(state.currentDateTime) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newTime = state.currentDateTime
                    .withHour(hourOfDay)
                    .withMinute(minute)
                viewModel.onEvent(MoodEvent.UpdateDateTime(newTime))
            },
            state.currentDateTime.hour,
            state.currentDateTime.minute,
            true
        )
    }

    LaunchedEffect(isDatePressed) {
        if (isDatePressed) datePickerDialog.show()
    }

    LaunchedEffect(isTimePressed) {
        if (isTimePressed) timePickerDialog.show()
    }

    LaunchedEffect(state.isSavedSuccessfully) {
        if (state.isSavedSuccessfully) {
            Toast.makeText(context, "Registro salvo com sucesso!", Toast.LENGTH_SHORT).show()
            viewModel.onEvent(MoodEvent.ResetSinks)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, "Erro: $it", Toast.LENGTH_LONG).show()
        }
    }

    MoodContent(
        state = state,
        dateFormatter = dateFormatter,
        timeFormatter = timeFormatter,
        dateInteractionSource = dateInteractionSource,
        timeInteractionSource = timeInteractionSource,
        onMoodSelected = { level -> viewModel.onEvent(MoodEvent.SelectMood(level)) },
        onNotesChange = { newNotes -> viewModel.onEvent(MoodEvent.UpdateNotes(newNotes)) },
        onSaveClick = { viewModel.onEvent(MoodEvent.SaveMood) },
        onInfoClick = {
            Log.d("MoodScreenDebug", "Ícone de info clicado! Alterando showDialog para true.")
            showDialog = true
        }
    )
}