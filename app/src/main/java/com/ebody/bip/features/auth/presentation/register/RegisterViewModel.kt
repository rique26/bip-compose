package com.ebody.bip.features.auth.presentation.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.asString
import com.ebody.bip.features.auth.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SubmitRegister -> {
                registerUser(
                    email = event.email,
                    password = event.password,
                    firstName = event.firstName,
                    lastName = event.lastName,
                    birthDate = event.birthDate,
                    phone = event.phone
                )
            }
            is RegisterEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        phone: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = registerUserUseCase(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDate,
                phone = phone
            )

            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
                }
                is Result.Error -> {
                    val errorMessage = result.error.asString(context)
                    _state.update { it.copy(isLoading = false, error = errorMessage) }
                }
            }
        }
    }
}