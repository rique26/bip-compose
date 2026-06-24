package com.ebody.bip.features.emergency.presentation.emergency_contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.features.emergency.domain.model.EmergencyContact
import com.ebody.bip.features.emergency.domain.usecase.DeleteContactUseCase
import com.ebody.bip.features.emergency.domain.usecase.GetEmergencyContactsUseCase
import com.ebody.bip.features.emergency.domain.usecase.SaveEmergencyContactUseCase
import com.ebody.bip.features.emergency.domain.usecase.ToggleSmsUseCase
import com.ebody.bip.features.emergency.domain.usecase.ToggleWhatsAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getEmergencyContactsUseCase: GetEmergencyContactsUseCase,
    private val toggleWhatsAppUseCase: ToggleWhatsAppUseCase,
    private val toggleSmsUseCase: ToggleSmsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val saveEmergencyContactUseCase: SaveEmergencyContactUseCase
) : ViewModel() {

    // Exposição reativa da lista de contatos, com tratamento de loading e erros
    val uiState: StateFlow<ContactsUiState> = getEmergencyContactsUseCase()
        .map { contacts ->
            ContactsUiState(contacts = contacts, isLoading = false)
        }
        .onStart { emit(ContactsUiState(isLoading = true)) }
        .catch { e ->
            emit(ContactsUiState(errorMessage = e.message ?: "Erro ao carregar contatos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ContactsUiState(isLoading = true)
        )

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.ToggleWhatsApp -> {
                viewModelScope.launch {
                    toggleWhatsAppUseCase(event.contactId)
                }
            }
            is ContactsEvent.ToggleSms -> {
                viewModelScope.launch {
                    toggleSmsUseCase(event.contactId)
                }
            }
            is ContactsEvent.DeleteContact -> {
                viewModelScope.launch {
                    deleteContactUseCase(event.contactId)
                }
            }
            is ContactsEvent.RefreshList -> {
                // Como o fluxo é reativo observando o banco/Room via Flow,
                // ele se atualiza sozinho automaticamente ao sofrer alterações.
            }
            is ContactsEvent.SaveContact -> {
                viewModelScope.launch {
                    val newContact = EmergencyContact(
                        id = "0L",
                        name = event.name,
                        phoneNumber = event.phoneNumber,
                        isWhatsAppEnabled = false,
                        isSmsEnabled = false
                    )
                    saveEmergencyContactUseCase(newContact)
                }
            }
        }
    }
}