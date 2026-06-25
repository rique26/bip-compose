package com.ebody.bip.features.wellbeing.presentation.mood

import java.time.LocalDateTime

data class MoodUiState(
    val selectedMood: Int? = null,
    val currentDateTime: LocalDateTime = LocalDateTime.now(),
    val notes: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSavedSuccessfully: Boolean = false
)