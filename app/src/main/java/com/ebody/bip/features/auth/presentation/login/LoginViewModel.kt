package com.ebody.bip.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.util.onError
import com.ebody.bip.core.domain.util.onSuccess
import com.ebody.bip.features.auth.domain.model.LoginRequest
import com.ebody.bip.features.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is LoginUiEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            LoginUiEvent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            LoginUiEvent.OnLogin -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _effect.emit(LoginEffect.NavigateToHome)

//            _uiState.update {
//                it.copy(isLoading = true)
//            }
//
//            val request = LoginRequest(
//                email = _uiState.value.email,
//                password = _uiState.value.password
//            )
//
//            loginUseCase(request)
//                .onSuccess {
//                    _uiState.update { it.copy(isLoading = false) }
//                    _effect.emit(LoginEffect.NavigateToHome)
//                }
//                .onError { authError ->
//                    _uiState.update { it.copy(isLoading = false) }
//                    _effect.emit(LoginEffect.ShowError(authError))
//                }
        }
    }
}

