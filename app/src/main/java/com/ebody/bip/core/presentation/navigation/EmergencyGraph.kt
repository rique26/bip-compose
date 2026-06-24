package com.ebody.bip.core.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.emergency.presentation.emergency.EmergencyScreen
import com.ebody.bip.features.emergency.presentation.emergency_contacts.EmergencyContactsScreen

fun NavGraphBuilder.emergencyGraph(navController: NavController) {
    navigation<EmergencyGraph>(startDestination = Emergency) {
        composable<Emergency> {
            EmergencyScreen(
                onNavigateToContacts = {
                    navController.navigate(EmergencyContacts)
                },
            )
        }

        composable<EmergencyContacts> {
            EmergencyContactsScreen(
                onNavigateToAddContact = {
                },
            )
        }
    }
}