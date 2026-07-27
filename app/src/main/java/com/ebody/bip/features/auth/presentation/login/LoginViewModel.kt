package com.ebody.bip.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.util.onError
import com.ebody.bip.core.domain.util.onSuccess
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.auth.domain.model.LoginRequest
import com.ebody.bip.features.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
)

sealed interface LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    data object OnTogglePasswordVisibility : LoginEvent
    data object OnLoginClick : LoginEvent
}

sealed interface LoginEffect {
    data object NavigateToHome : LoginEffect
    data class ShowError(val error: BipAuthException) : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.email) }
            is LoginEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.password) }
            LoginEvent.OnTogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginEvent.OnLoginClick -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            loginUseCase(
                email = _uiState.value.email,
                password = _uiState.value.password
            )
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(LoginEffect.NavigateToHome)
                }
                .onError { authError ->
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(LoginEffect.ShowError(authError))
                }
        }
    }
}

