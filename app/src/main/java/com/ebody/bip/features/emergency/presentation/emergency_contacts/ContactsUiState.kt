package com.ebody.bip.features.emergency.presentation.emergency_contacts

import com.ebody.bip.features.emergency.domain.model.EmergencyContact

data class ContactsUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)