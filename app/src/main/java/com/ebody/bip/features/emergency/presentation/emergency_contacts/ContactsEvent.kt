package com.ebody.bip.features.emergency.presentation.emergency_contacts

sealed interface ContactsEvent {
    data class ToggleWhatsApp(val contactId: String) : ContactsEvent
    data class ToggleSms(val contactId: String) : ContactsEvent
    data class DeleteContact(val contactId: String) : ContactsEvent
    data class SaveContact(val name: String, val phoneNumber: String) : ContactsEvent
    object RefreshList : ContactsEvent
}