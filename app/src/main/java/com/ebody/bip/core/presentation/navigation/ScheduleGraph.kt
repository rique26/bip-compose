package com.ebody.bip.core.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.schedule.presentation.medication_list.MedicationListScreen

fun NavGraphBuilder.scheduleGraph(navController: NavController) {
    navigation<ScheduleGraph>(startDestination = MedicationList) {

        composable<MedicationList> {
            MedicationListScreen(navController, hiltViewModel())
        }

    }
}