package com.ebody.bip.navigation

import android.os.Handler
import android.os.Looper
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ebody.bip.features.schedule.presentation.home_dashboard.HomeDashboardScreen
import com.ebody.bip.features.schedule.presentation.medication_schedule.MedicationScheduleScreen
import com.ebody.bip.features.schedule.presentation.medication_selection.MedicationSelectionScreen

fun NavGraphBuilder.scheduleGraph(navController: NavController) {
    navigation<ScheduleGraph>(startDestination = Home) {

        composable<Home> {
            HomeDashboardScreen(
                onNavigateToMedicationSelection = {
                    navController.navigate(MedicationSelection)
                },
                onNavigateToEmergency = {
                    navController.navigate(Emergency)
                },
                onNavigateToMood = {
                    navController.navigate(HistoryRoute)
                }
            )
        }

        composable<MedicationSelection> {
            MedicationSelectionScreen(
                onNavigateToSchedule = { selectedIds ->
                    navController.navigate(MedicationSchedule(medicationIds = selectedIds))
                }
            )
        }

        composable<MedicationSchedule> { backStackEntry ->
            val args = backStackEntry.toRoute<MedicationSchedule>()
            MedicationScheduleScreen(
                medicationIds = args.medicationIds,
                onBack = { navController.popBackStack() },
                onFinish = {
                    Handler(Looper.getMainLooper()).post {
                        navController.navigate(Home) {
                            popUpTo<ScheduleGraph> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}