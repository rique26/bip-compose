package com.ebody.bip.core.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.wellbeing.presentation.MoodScreen
import androidx.hilt.navigation.compose.hiltViewModel

fun NavGraphBuilder.wellbeingGraph(navController: NavController) {
    navigation<WellbeingGraph>(startDestination = MoodRoute) {

        composable<MoodRoute> {
            MoodScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}