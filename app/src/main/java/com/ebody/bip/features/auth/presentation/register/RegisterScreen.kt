package com.ebody.bip.features.auth.presentation.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebody.bip.features.auth.presentation.register.components.RegisterFooter
import com.ebody.bip.features.auth.presentation.register.components.RegisterHeader
import com.ebody.bip.features.auth.presentation.register.components.RegisterStep1
import com.ebody.bip.features.auth.presentation.register.components.RegisterStep2
import com.ebody.bip.features.auth.presentation.register.components.RegisterStep3
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToRegistrationSuccess: () -> Unit,
    onBackClick: () -> Unit
) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.state.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isRegistrationSuccess) {
        if (uiState.isRegistrationSuccess) {
            onNavigateToRegistrationSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(message = errorMessage)
            viewModel.onEvent(RegisterEvent.ClearError)
        }
    }

    val handleBack = {
        if (pagerState.currentPage > 0) {
            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        } else {
            onBackClick()
        }
    }

    BackHandler(enabled = true) { handleBack() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            RegisterHeader(
                currentStep = pagerState.currentPage + 1,
                onBackClick = { handleBack() }
            )

            RegisterPagerContent(
                modifier = Modifier.weight(1f),
                pagerState = pagerState,
                firstName = firstName, onFirstNameChange = { firstName = it },
                lastName = lastName, onLastNameChange = { lastName = it },
                birthDate = birthDate, onBirthDateChange = { birthDate = it },
                email = email, onEmailChange = { email = it },
                phone = phone, onPhoneChange = { phone = it },
                password = password, onPasswordChange = { password = it }
            )

            RegisterFooter(
                isLastStep = pagerState.currentPage == 2,
                isLoading = uiState.isLoading,
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.onEvent(
                            RegisterEvent.SubmitRegister(
                                email = email,
                                password = password,
                                firstName = firstName,
                                lastName = lastName,
                                birthDate = birthDate,
                                phone = phone
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun RegisterPagerContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    birthDate: String, onBirthDateChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        userScrollEnabled = false,
        verticalAlignment = Alignment.Top
    ) { page ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when (page) {
                0 -> RegisterStep1(
                    firstName,
                    onFirstNameChange,
                    lastName,
                    onLastNameChange,
                    birthDate,
                    onBirthDateChange
                )

                1 -> RegisterStep2(email, onEmailChange, phone, onPhoneChange)
                2 -> RegisterStep3(password, onPasswordChange)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onNavigateToRegistrationSuccess = {},
        onBackClick = {}
    )
}

