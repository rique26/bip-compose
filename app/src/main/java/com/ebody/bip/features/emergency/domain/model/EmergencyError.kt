package com.ebody.bip.features.emergency.domain.model

sealed interface EmergencyError {
    object Network : EmergencyError
    object Database : EmergencyError
    object NotFound : EmergencyError
    data class Unknown(val message: String?) : EmergencyError
}