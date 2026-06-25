package com.ebody.bip.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.auth.presentation.login.LoginScreen
import com.ebody.bip.features.auth.presentation.register.RegisterScreen
import com.ebody.bip.features.auth.presentation.register.RegistrationSuccessScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthGraph>(startDestination = Login) {

        composable<Login> {

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ScheduleGraph) {
                        popUpTo(AuthGraph) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Register)
                }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToRegistrationSuccess = {
                    navController.navigate(RegistrationSuccess)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<RegistrationSuccess> {
            RegistrationSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(ScheduleGraph){
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}