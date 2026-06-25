package com.ebody.bip.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ebody.bip.features.wellbeing.presentation.analytics.AnalyticsScreen
import com.ebody.bip.features.wellbeing.presentation.history.HistoryScreen
import com.ebody.bip.features.wellbeing.presentation.mood.MoodScreen

fun NavGraphBuilder.wellbeingGraph(navController: NavController) {
    navigation<WellbeingGraph>(startDestination = HistoryRoute) {

        composable<MoodRoute> {
            MoodScreen(
                viewModel = hiltViewModel(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<HistoryRoute> {
            HistoryScreen(
                viewModel = hiltViewModel(),
                onNavigateToMoodEntry = {
                    navController.navigate(MoodRoute)
                },
                onNavigateToStats = {
                    navController.navigate(AnalyticsRoute)
                }
            )
        }

        composable<AnalyticsRoute> {
            AnalyticsScreen(
                viewModel = hiltViewModel()
            )
        }

    }
}