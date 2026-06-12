package com.ebody.bip.core.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.auth.presentation.login.LoginScreen
import com.ebody.bip.features.auth.presentation.register.RegisterScreen

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
                onNavigateToSchedule = {
                    navController.navigate(ScheduleGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}