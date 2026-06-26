package com.ebody.bip.features.wellbeing.domain.model

sealed interface WellbeingError {
    object Network : WellbeingError
    object Database : WellbeingError
    object NotFound : WellbeingError
    data class Unknown(val message: String?) : WellbeingError
}