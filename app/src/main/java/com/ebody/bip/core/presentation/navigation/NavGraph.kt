package com.ebody.bip.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavGraph(
    navController: NavHostController,
    isAuthenticated: Boolean
    ) {

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) ScheduleGraph else AuthGraph
//        startDestination = ScheduleGraph
    ) {
        authGraph(navController)
        scheduleGraph(navController)
    }

}