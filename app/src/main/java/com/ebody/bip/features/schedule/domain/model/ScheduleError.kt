package com.ebody.bip.features.schedule.domain.model

sealed interface ScheduleError {
    object Network : ScheduleError
    object Database : ScheduleError
    object NotFound : ScheduleError
    data class Unknown(val message: String?) : ScheduleError
}