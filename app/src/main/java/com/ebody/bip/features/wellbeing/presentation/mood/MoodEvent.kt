package com.ebody.bip.features.wellbeing.presentation.mood

import java.time.LocalDateTime

sealed interface MoodEvent {
    data class SelectMood(val mood: Int) : MoodEvent
    data class UpdateDateTime(val dateTime: LocalDateTime) : MoodEvent
    data class UpdateNotes(val notes: String) : MoodEvent
    object SaveMood : MoodEvent
    object ResetSinks : MoodEvent
}